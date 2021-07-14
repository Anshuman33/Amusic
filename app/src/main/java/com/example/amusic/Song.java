package com.example.amusic;

import android.graphics.Bitmap;
import android.net.Uri;

public class Song {
    private Bitmap albumArt;
    private Uri songUri;
    private String name;
    private int isLiked;
    Song(Uri uri,Bitmap bitmap,String name){
        songUri = uri;
        albumArt = bitmap;
        this.name = name;
        isLiked = 0;
    }
    Song(Uri uri,Bitmap bitmap,String name,int likeState){
        songUri = uri;
        albumArt = bitmap;
        this.name = name;
        isLiked = likeState;
    }
    int getIsLiked(){
        return isLiked;
    }
    Uri getSongUri(){
        return  songUri;
    }
    String getSongName(){
        return name;
    }
    Bitmap getSongArt(){
        return albumArt;
    }
}
