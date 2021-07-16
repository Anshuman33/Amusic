package com.example.amusic;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

// Playlist model class
public class Playlist{
    String playlistName;
    int currentIndex;
    List<Song> songList;

    Playlist(){
        playlistName = "DefaultPlaylistName";
        songList = new ArrayList<>();
        currentIndex=0;
    }
    Playlist(String name){
        playlistName = name;
        songList = new ArrayList<>();
        currentIndex = 0;
    }

    public void addNewSong(Song song){
        songList.add(song);
    }

    public Song getSong(int position){
        return songList.get(position);
    }

    public void deleteSong(int position){
            songList.remove(position);
    }
    public void deleteSong(Song song){
        songList.remove(song);
    }

    public Uri getSongUri(int pos){
        return songList.get(pos).getSongUri();
    }
    public Bitmap getSongArt(int pos){
        return songList.get(pos).getSongArt();
    }
    public String getSongName(int pos){
        return songList.get(pos).getSongName();
    }
    public int getSongLikeStatus(int pos){ return songList.get(pos).getIsLiked(); }

    public int getNumberOfSongs(){
        return songList.size();
    }

}
