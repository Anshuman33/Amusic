package com.example.amusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CurrentPlaylist extends Fragment {
    MyBinder myBinder;
    Playlist playlist,favPlaylist;
    PlaylistDatabaseHelper helper;

    CurrentPlaylist(MyBinder binder,Playlist playlist,Playlist favPlaylist,PlaylistDatabaseHelper helper){
        myBinder = binder;
        this.playlist = playlist;
        this.favPlaylist = favPlaylist;
        this.helper = helper;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("MYDEBUG","Current Playlist Create");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("MYDEBUG","Current Playlist OnSTART");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.current_playlist,container,false);
        RecyclerView recyclerView = rootView.findViewById(R.id.playlistRecycler);
        Log.v("DEBUG",myBinder.getService().toString());
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(myBinder,playlist,favPlaylist,getContext(),helper);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.setAdapter(playlistAdapter);
        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("CurrentPlaylist","Destroyed");
    }
}
