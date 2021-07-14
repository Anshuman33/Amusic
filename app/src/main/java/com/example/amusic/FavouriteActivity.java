package com.example.amusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavouriteActivity extends Fragment {
    Playlist favouritePlaylist ;
    RecyclerView recyclerView;
    MyBinder mBinder;
    static FavouriteAdapter playlistAdapter;


    FavouriteActivity(MyBinder binder,Playlist playlist){
        favouritePlaylist = playlist;
        mBinder = binder;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.favourite_playlist,container,false);
        recyclerView = (rootview.findViewById(R.id.favouriteRecycler));

        playlistAdapter = new FavouriteAdapter(mBinder,favouritePlaylist);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootview.getContext()));
        recyclerView.setAdapter(playlistAdapter);


        return rootview;
    }
}
