package com.example.amusic;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.OpenableColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyBinder extends Binder {
    Context context;
    Playlist playlist;
    MediaPlayer mp;
    MyService mService;
    boolean controllerUiChange = false;
    //List<Bitmap> albumarts ;
    //List<Uri> uriArr ;
    //List<String> songNameArr;
    int currentIndex;
    boolean songChange = false;

    MyBinder(MyService service){
        mService = service;
    }

    // To play the playlist
    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    MyService getService(){
        return mService;
    }
    public void setContext(Context context) {
        this.context = context;
        //songNameArr = new ArrayList<>();
    }


    public Context getContext() {
        return context;
    }

    void startPlaylist(){
        currentIndex = 0;
        initMediaPlayer(getCurrentSongUri());
    }

    public int getmpDuration(){
        if(mp!=null){
            return mp.getDuration();
        }
        else return 0;
    }

    public int getCurrentPos(){
        if(mp!=null){
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


    void play(){
        if(mp!=null){
            mp.start();
        }
    }
    void pause(){
        if(mp!=null && mp.isPlaying()){
            mp.pause();
        }
    }
    void next(){
        if(!playlist.songList.isEmpty()){
            currentIndex++;
            currentIndex = currentIndex % playlist.songList.size();
            initMediaPlayer(playlist.getSongUri(currentIndex));
            play();
        }
    }
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
    void initMediaPlayer(Uri uri){
        if(mp!=null){
            mp.reset();
        }
        mp = MediaPlayer.create(getContext(),uri);
        songChange = true;
        controllerUiChange = true;
        //getAlbumArt(uri);
        //MainActivity.songTitle.setText(songNameArr.get(currentIndex));
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


    String getSongName(Uri uri){
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
