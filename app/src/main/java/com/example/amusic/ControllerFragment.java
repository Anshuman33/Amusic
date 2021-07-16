package com.example.amusic;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ControllerFragment extends Fragment {
    MyBinder mBinder;
    TextView songTitle;
    ImageButton prevButton;
    ImageButton playButton;
    ImageButton nextButton;
    Handler UiUpdaterHandler;
    Runnable UiUpdater;




    public void setBinder(MyBinder binder){
        mBinder = binder;
        if(mBinder != null){
            songTitle.setText(mBinder.getCurrentSongName());
        }
        UiUpdaterHandler.postAtFrontOfQueue(UiUpdater);
    }
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("CONTROLLER","Controller:OnCreate()");

        UiUpdaterHandler = new Handler();
        UiUpdater = new Runnable() {
            @Override
            public void run() {
                if(mBinder!=null){
                    if(mBinder.controllerUiChange)
                    {
                        songTitle.setText(mBinder.getCurrentSongName());
                        playButton.setImageResource(R.drawable.pause_symbol);
                        mBinder.controllerUiChange = false;
                    }
                }
                UiUpdaterHandler.postDelayed(this,50);
            }
        };


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.controller_layout,container,false);
        songTitle = rootView.findViewById(R.id.controllerSongTitle);
        songTitle.setSelected(true);
        Log.v("CONTROLLER","Controller:OnCreateView()");
        if(mBinder!=null)
            songTitle.setText(mBinder.getCurrentSongName());
        else{
            songTitle.setText("No song playing");
        }
        prevButton = rootView.findViewById(R.id.controllerPrev);
        nextButton = rootView.findViewById(R.id.controllerNext);
        playButton = rootView.findViewById(R.id.controllerPlay);

        //set on click listeners for all the buttons

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBinder.playlist.songList.isEmpty()){
                    Toast.makeText(getContext(),"No song in the playlist",Toast.LENGTH_SHORT).show();
                }
                if(mBinder.mp!=null){
                    mBinder.prev();
                    songTitle.setText(mBinder.getCurrentSongName());
                    playButton.setImageResource(R.drawable.pause_symbol);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBinder.playlist.songList.isEmpty()){
                    Toast.makeText(getContext(),"No song in the playlist",Toast.LENGTH_SHORT).show();
                }
                if(mBinder.mp!=null){
                    mBinder.next();
                    songTitle.setText(mBinder.getCurrentSongName());
                    playButton.setImageResource(R.drawable.pause_symbol);
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBinder.playlist.songList.isEmpty()){
                    Toast.makeText(getContext(),"No song in the playlist",Toast.LENGTH_SHORT).show();
                }
                if(mBinder.mp!=null){
                    if(mBinder.mp.isPlaying()){
                        mBinder.pause();
                        playButton.setImageResource(R.drawable.play_button);
                        UiUpdaterHandler.removeCallbacks(UiUpdater);
                    }
                    else{
                        mBinder.play();
                        UiUpdaterHandler.postDelayed(UiUpdater,50);
                        playButton.setImageResource(R.drawable.pause_symbol);
                    }
                }
            }
        });
        return rootView.findViewById(R.id.controller);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("CONTROLLER","Controller:OnResume()");

    }

    @Override
    public void onStart() {
        Log.v("CONTROLLER","Controller:OnStart()");
        super.onStart();


    }
}
