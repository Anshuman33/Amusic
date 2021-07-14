package com.example.amusic;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Playlist{
    String playlistName;
    /*List<Uri> uriList;
    List<String> songnameList;
    List<Bitmap> albumArtList;*/
    int currentIndex;

    List<Song> songList;
    Playlist(){
        playlistName = "DefaultPlaylistName";
        /*uriList = new ArrayList<>();
        songnameList = new ArrayList<>();
        albumArtList = new ArrayList<>();*/
        songList = new ArrayList<>();
        currentIndex=0;
    }
    Playlist(String name){
        playlistName = name;
        /*uriList = new ArrayList<>();
        songnameList = new ArrayList<>();
        albumArtList = new ArrayList<>();*/
        songList = new ArrayList<>();
        currentIndex = 0;
    }

    /*public List<Bitmap> getAlbumArtList() {
        return albumArtList;
    }

    public List<String> getSongnameList() {
        return songnameList;
    }

    public List<Uri> getUriList() {
        return uriList;
    }

    public Uri getCurrentUri() {
        return uriList.get(currentIndex);
    }*/

    public void addNewSong(Song song){
        songList.add(song);
        /*uriList.add(song.getSongUri());
        albumArtList.add(song.getSongArt());
        songnameList.add(song.getSongName());*/
    }

    public Song getSong(int position){
        //return new Song(uriList.get(position),albumArtList.get(position),songnameList.get(position));
        return songList.get(position);
    }

    public void deleteSong(int position){
            /*songnameList.remove(position);
            uriList.remove(position);
            uriList.remove(position);*/
            songList.remove(position);
    }
    public void deleteSong(Song song){
        /*songnameList.remove(song.getSongName());
        uriList.remove(song.getSongUri());
        albumArtList.remove(song.getSongArt());*/
        songList.remove(song);
    }

    /*public void addSong(Uri uri){
        uriList.add(uri);
    }

    public void addAlbumArt(Bitmap bitmap){
        albumArtList.add(bitmap);
    }
    public void addSongName(String name){
        songnameList.add(name);
    }*/

    public Uri getSongUri(int pos){
        return songList.get(pos).getSongUri();
    }
    public Bitmap getSongArt(int pos){
        return songList.get(pos).getSongArt();
    }
    public String getSongName(int pos){
        return songList.get(pos).getSongName();
    }

    public int getNumberOfSongs(){
        return songList.size();
    }

}
