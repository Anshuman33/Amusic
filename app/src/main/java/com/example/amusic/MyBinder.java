package com.example.amusic;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.provider.OpenableColumns;
import android.util.Log;


/*
    This is the binder class for the music service.
    It consists of all the functions to control the music player.
 */
public class MyBinder extends Binder {
    Context context;
    Playlist playlist;
    MediaPlayer mp;
    MyService mService;
    boolean controllerUiChange = false;
    int currentIndex;
    boolean songChange = false;

    // Constructor which initializes the service
    MyBinder(MyService service){
        mService = service;
    }

    // Getter for associated service
    MyService getService(){
        return mService;
    }

    // Setter for context
    public void setContext(Context context) {
        this.context = context;
    }

    // Getter for context
    public Context getContext() {
        return context;
    }

    // Sets the playlist variable
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    // Initializes the media player with the song uri
    void initMediaPlayer(Uri uri){
        if(mp != null) {
            mp.reset();
        }
        mp = MediaPlayer.create(getContext(),uri);
        songChange = true;
        controllerUiChange = true;

        // Set the listener to play the next song after current song is complete
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                currentIndex++;
                currentIndex = currentIndex % playlist.songList.size();
                initMediaPlayer(playlist.getSongUri(currentIndex));
                play();
            }
        });
    }

    // Start the current playlist
    void startPlaylist(){
        currentIndex = 0;
        initMediaPlayer(getCurrentSongUri());
    }

    // Get the duration of current media player
    public int getmpDuration(){
        if(mp != null){
            return mp.getDuration();
        }
        else return 0;
    }

    // Get current position of the media player
    public int getCurrentPos(){
        if(mp != null){
            return mp.getCurrentPosition();
        }
        else return 0;
    }


    Uri getCurrentSongUri(){
        return playlist.getSongUri(currentIndex);
    }

    String getCurrentSongName(){
        return playlist.getSongName(currentIndex);
    }

    // Starts playing the media player
    void play(){
        if(mp != null && !mp.isPlaying()){
            mp.start();
        }
    }

    // Pause the media player
    void pause(){
        if(mp != null && mp.isPlaying()){
            mp.pause();
        }
    }

    // Move to next song in the playlist
    void next(){
        if(!playlist.songList.isEmpty()){
            currentIndex++;
            currentIndex = currentIndex % playlist.songList.size();
            initMediaPlayer(playlist.getSongUri(currentIndex));
            play();
        }
    }

    // Move to previous song in the playlist
    void prev(){
        if(!playlist.songList.isEmpty()){
            currentIndex--;
            if(currentIndex == -1){
                currentIndex = playlist.getNumberOfSongs()-1;
            }
            initMediaPlayer(playlist.getSongUri(currentIndex));
            play();
        }
    }

    Bitmap getCurrentAlbumArt(){
        return playlist.getSongArt(currentIndex);
    }
    /*void getAlbumArt(Uri uri){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getContext(),uri);

        byte[] data = mmr.getEmbeddedPicture();
        if(data!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
            playlist.addAlbumArt(bitmap);
        }
        else{
            playlist.addAlbumArt(BitmapFactory.decodeResource(context.getResources(),R.drawable.default1));
        }

    }*/


    String getSongDetails(Uri uri){
        // Currently only title is fetched but more can be added
        Cursor cursor = context.getContentResolver().query(uri,null,null,null,null,null);
        try{
            if(cursor!=null && cursor.moveToFirst()){
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i("DisplayName",displayName);
                return displayName;

            }
        }finally {
            cursor.close();
        }
        return null;
    }
}
