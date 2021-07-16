package com.example.amusic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {


    MyBinder myBinder = null;
    ImageView albumArt;
    private static boolean firstTimeSongsSelected = true;
    ImageButton startButton,nextButton,prevButton;
    FloatingActionButton addSong;
    TextView songTitle,currentTime,maxTime;
    SeekBar seekBar;
    Handler seekbarUpdaterHandler;
    Runnable seekbarUpdater;
    PlaylistDatabaseHelper helper;


    private final int SONG_FETCH_REQUEST_CODE = 255;

    // Creates the connection object that establishes the audio service connection
    private ServiceConnection serviceConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myBinder = (MyBinder)iBinder;
            myBinder.setContext(getApplicationContext());
            myBinder.setPlaylist(loadCurrentPlaylist());
            if(myBinder.playlist.songList.size()>0){
                myBinder.startPlaylist();
                updateUI();
            }
            //loadSongs();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    public void bindAudioService(){
        if(myBinder == null) {
            Intent i = new Intent(getApplicationContext(),MyService.class);
            bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }
    private void unbindAudioService(){
        if(myBinder!=null){
            unbindService(serviceConnection);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindAudioService();

        // Initialize the view variables
        albumArt = findViewById(R.id.mainAlbumArt);
        songTitle = findViewById(R.id.SongTitle);
        startButton = findViewById(R.id.playButton);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        addSong = findViewById(R.id.addSongButton);
        currentTime = findViewById(R.id.currentTime);
        maxTime = findViewById(R.id.maxTime);

        // Initialize the Playlist Database Helper
        helper = new PlaylistDatabaseHelper(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.playlistMenu){
            Intent i = new Intent(getApplicationContext(),PlaylistActivity.class);
            startActivity(i);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
     protected void onStart(){
         super.onStart();
         songTitle.setSelected(true);
         seekBar = findViewById(R.id.seekBar);

         seekbarUpdaterHandler = new Handler();
         seekbarUpdater = new Runnable() {
             @Override
             public void run() {
                 if(myBinder.mp != null){

                     // Update the current position of the seekbar and timer
                     int timecurrent = myBinder.getCurrentPos();
                     seekBar.setProgress(timecurrent);
                     currentTime.setText(toMinute(timecurrent));

                     // Change the play symbol to pause symbol or vice-versa
                     if(myBinder.mp.isPlaying())
                         startButton.setImageResource(R.drawable.pause_symbol);
                     else
                         startButton.setImageResource(R.drawable.play_button);

                     // If the song has been changed then update the current song UI
                     if(myBinder.songChange) {
                         Uri u = myBinder.getCurrentSongUri();
                         albumArt.setImageBitmap(myBinder.getCurrentAlbumArt());
                         songTitle.setText(myBinder.getCurrentSongName());
                         int timemax = myBinder.mp.getDuration();
                         maxTime.setText(toMinute(timemax));
                         seekBar.setMax(timemax);
                         myBinder.songChange = false;
                     }
                     seekbarUpdaterHandler.postDelayed(this,50);

                 }
             }
         };

         // Update the media player position when seek bar is slided
         seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                 if(b) {
                     if (myBinder.mp != null) {
                         myBinder.mp.seekTo(i);
                     }
                 }
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {

             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {

             }
         });
     }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myBinder.mp.release();
        savePlaylist();
        helper.close();
        Log.v("MainActivity","Destroyed");
        unbindAudioService();
    }

    public void addSongFunc(View view) {
        /*
            Called when add song button is clicked to add more songs into the playlist.
            Opens an intent to fetch audio files from file manager
        */
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("audio/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(i, SONG_FETCH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /*
            Called when add song intent returns. If some file is selected then the
            URI of the file is fetched and the song is loaded.
        */
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SONG_FETCH_REQUEST_CODE && resultCode == RESULT_OK){
            if(data!=null){
                ClipData clipData = data.getClipData();
                if(clipData == null){
                    // If only one file is selected, get the song details and add to the current
                    // playlist.
                    Uri uri = data.getData();

                    if(uri!=null){

                        String name = getSongName(uri);
                        Bitmap bitmap = getAlbumArt(uri);
                        myBinder.playlist.addNewSong(new Song(uri,bitmap,name));
                    }
                    Log.v("MainActivity","Clip Data Null");
                }
                else {
                    // If more than one song files are selected get song details for each song
                    // and then add the song to the current playlist
                    for(int i = 0;i<clipData.getItemCount();i++){
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri u = item.getUri();
                        String name = getSongName(u);
                        Bitmap bitmap = getAlbumArt(u);
                        myBinder.playlist.addNewSong(new Song(u,bitmap,name));

                    }

                }

                if(firstTimeSongsSelected) {
                    // If this is the first song added to the playlist, play it as current song
                    myBinder.initMediaPlayer(myBinder.getCurrentSongUri());
                    seekBar.setMax(myBinder.getmpDuration());
                    seekbarUpdaterHandler.postDelayed(seekbarUpdater, 0);
                    myBinder.currentIndex=0;
                    myBinder.play();
                    startButton.setImageResource(R.drawable.pause_symbol);
                    firstTimeSongsSelected = false;
                }

            }
        }
        savePlaylist();
    }

    public void PlayButton(View view){
        if(myBinder.playlist.songList.isEmpty()){
            Toast.makeText(getApplicationContext(),"No song in playlist", Toast.LENGTH_SHORT).show();
        }
        else{
            if(myBinder.mp!=null) {
                if (myBinder.mp.isPlaying()) {
                    myBinder.pause();
                    seekbarUpdaterHandler.removeCallbacks(seekbarUpdater);
                    startButton.setImageResource(R.drawable.play_button);
                }
                else{
                    seekbarUpdaterHandler.postDelayed(seekbarUpdater,0);
                    myBinder.play();
                    startButton.setImageResource(R.drawable.pause_symbol);
                }
                myBinder.controllerUiChange = true;
            }

        }
    }

    public void nextButton(View v){
        if(myBinder.playlist.getNumberOfSongs()<1){
            Toast.makeText(this,"No songs in playlist",Toast.LENGTH_SHORT).show();

        }
        else {
            myBinder.next();
            seekbarUpdaterHandler.postDelayed(seekbarUpdater, 0);
            startButton.setImageResource(R.drawable.pause_symbol);
        }
    }
    public void prevButton(View v){
        if(myBinder.playlist.getNumberOfSongs()<1){
            Toast.makeText(this,"No songs in playlist",Toast.LENGTH_SHORT).show();
        }
        else {
            myBinder.prev();
            seekbarUpdaterHandler.postDelayed(seekbarUpdater, 0);
            startButton.setImageResource((R.drawable.pause_symbol));
        }
    }

    void updateUI(){
        // Updates the song UI i.e. the song name and the album art
        albumArt.setImageBitmap(myBinder.getCurrentAlbumArt());
        songTitle.setText(myBinder.getCurrentSongName());
    }

    String toMinute(int time){
        // Converts integer time into mm:ss format for display
        time = time / 1000;
        String myStr="";
        DecimalFormat formatter = new DecimalFormat("00");
        myStr += time / 60 + ":" + formatter.format(time % 60);
        return myStr;
    }


    public void savePlaylist(){
        // Saves the current playlist in the database
        for(int i=0;i<myBinder.playlist.getNumberOfSongs();i++){
            helper.insertData(PlaylistDatabaseHelper.PLAYLIST_1,myBinder.playlist.getSong(i));
        }
    }
    String getSongName(Uri uri){
        // Currently only title is fetched but more can be added
        Cursor cursor = getContentResolver().query(uri,null,null,null,null,null);
        try{
            if(cursor!=null && cursor.moveToFirst()){
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i("DisplayName",displayName);
                return displayName;

            }
        }finally {
            cursor.close();
        }
        return "DefaultSongName";
    }


    Bitmap getAlbumArt(Uri uri){
        /*
            Fetches the album art (if any) from the song URI
        */
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try{
            mmr.setDataSource(this, uri);
            byte[] data = mmr.getEmbeddedPicture();
            if(data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                return bitmap;
            }
            else{
                return BitmapFactory.decodeResource(getResources(),R.drawable.default1);
            }
        }catch (Exception e) {
            Log.i("MYDEBUG", e.toString());
            return BitmapFactory.decodeResource(getResources(), R.drawable.default1);
        }
    }

    public Playlist loadCurrentPlaylist(){
        /*
            Loads the current playlist from the database
        */
        Cursor cursor = helper.retrieveAll(PlaylistDatabaseHelper.PLAYLIST_1);
        Playlist playlist = new Playlist("Current");

        if(cursor.getCount()!=0){
            while(cursor.moveToNext()){
                String name = cursor.getString(1);
                Uri uri= Uri.parse(cursor.getString(2));
                int isLiked = cursor.getInt(3);
                Log.i("MYDEBUG",name + " " + uri.toString());
                Bitmap bitmap = getAlbumArt(uri);
                Song song = new Song(uri,bitmap,name);
                playlist.addNewSong(song);
            }
        }
        return playlist;
    }
    @Override
    public void onBackPressed() {

    }


}
