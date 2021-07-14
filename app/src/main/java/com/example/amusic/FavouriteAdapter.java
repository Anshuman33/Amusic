package com.example.amusic;



import android.graphics.Bitmap;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.PlaylistViewHolder> {
    MyBinder mBinder;
    Playlist playlist;

    FavouriteAdapter(MyBinder binder){
        mBinder = binder;
        this.playlist = binder.playlist;
    }
    FavouriteAdapter(MyBinder binder,Playlist playlist){
        mBinder = binder;
        this.playlist = playlist;
    }
    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.playlist_item2,parent,false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlaylistViewHolder holder, final int position) {
        Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = playlist.getSongArt(position);
                if(bitmap!=null){
                    holder.img.setImageBitmap(bitmap);
                }
                else{
                    holder.img.setImageResource(R.drawable.default1);
                }
            }

        });

        holder.songText.setText(playlist.getSongName(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Handler handler = new Handler(Looper.myLooper());
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        mBinder.setPlaylist(playlist);
                        mBinder.currentIndex = position;
                        mBinder.initMediaPlayer(playlist.getSongUri(position));
                        mBinder.play();
                    }
                });
            }

        });


    }

    @Override
    public int getItemCount() {
        return playlist.getNumberOfSongs();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder{
        TextView songText;
        ImageView img;
        CheckBox checkBox;
        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            songText = itemView.findViewById(R.id.songText);
            img = itemView.findViewById(R.id.albumArt);
            checkBox = itemView.findViewById(R.id.heartCheck);
        }
    }
}
