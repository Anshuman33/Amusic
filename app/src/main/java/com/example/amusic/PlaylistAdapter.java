package com.example.amusic;


import android.content.Context;
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

/*
    This is the adapter class for displaying playlists. It takes in the playlist object and fetches
    the required data for the UI to render.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    MyBinder mBinder;
    Playlist playlist,favPlaylist;
    PlaylistDatabaseHelper helper;


    PlaylistAdapter(MyBinder binder,Playlist favPlaylist){
        mBinder = binder;
        this.playlist = binder.playlist;
        this.favPlaylist = favPlaylist;

    }
    PlaylistAdapter(MyBinder binder, Playlist playlist, Playlist favPlaylist, Context context,PlaylistDatabaseHelper helper){
        mBinder = binder;
        this.playlist = playlist;
        this.favPlaylist = favPlaylist;
        this.helper = helper;

    }
    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates individual playlist item view from resources
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.playlist_item, parent,false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlaylistViewHolder holder, final int position) {
        Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Set the bitmap for playlist item
                Bitmap bitmap = playlist.getSongArt(position);
                if(bitmap != null){
                    holder.img.setImageBitmap(bitmap);
                }
                else{
                    holder.img.setImageResource(R.drawable.default1);
                }
            }

        });


        holder.songText.setText(playlist.getSongName(position));

        // Play the respective song when the song's holder is clicked
        holder.itemView.setOnClickListener(view -> {
            Handler handler1 = new Handler(Looper.myLooper());
            handler1.post(() -> {
                mBinder.setPlaylist(playlist);
                mBinder.currentIndex = position;
                mBinder.initMediaPlayer(playlist.getSongUri(position));
                mBinder.play();
            });
        });

        // If the song is already marked as liked, check the checkbox
        if(playlist.getSongLikeStatus(position) == 1){
            holder.checkBox.setChecked(true);
        }
        else{
            holder.checkBox.setChecked(false);
        }

        // When the song is liked, add the song to the Favourite's playlist
        // and remove song when deselected.
        holder.checkBox.setOnCheckedChangeListener((compoundButton, state) -> {
            if (state) {
                Log.v("MYDEBUG", "Song Liked");
                favPlaylist.addNewSong(new Song(playlist.getSongUri(position), playlist.getSongArt(position), playlist.getSongName(position)));
                helper.insertData(PlaylistDatabaseHelper.PLAYLIST_2, playlist.getSong(position));
                FavouriteActivity.playlistAdapter.notifyDataSetChanged();

            } else {
                Log.v("MYDEBUG", "Song removed from liked songs");
                favPlaylist.deleteSong(new Song(playlist.getSongUri(position), playlist.getSongArt(position), playlist.getSongName(position)));
                helper.deleteSongByName(PlaylistDatabaseHelper.PLAYLIST_2, playlist.getSongName(position));
                FavouriteActivity.playlistAdapter.notifyDataSetChanged();

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
