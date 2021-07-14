package com.example.amusic;

import android.app.Service;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.IBinder;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.amusic.ui.main.SectionsPagerAdapter;

import java.util.List;

public class PlaylistActivity extends AppCompatActivity {

    // Flag for bound state
    boolean mBound = false;
    MyBinder myBinder;
    PlaylistDatabaseHelper helper;
    // Playlist list contains all the playlist
    private static final int NO_OF_PLAYLIST=2;
    Playlist[] playlistArray = new Playlist[NO_OF_PLAYLIST];

    // Song controller fragment
    ControllerFragment controllerFragment;

    // Adapter for displaying the tabs
    SectionsPagerAdapter sectionsPagerAdapter;

    //Establish the service connection
     ServiceConnection serviceConnection = new ServiceConnection() {
         @Override
         public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
             myBinder = (MyBinder)iBinder;
             Log.v("MYDEBUG","Bound Service");
             mBound = true;
             playlistArray[0] = myBinder.playlist;
             playlistArray[1] = loadFavouritePlaylist();

             sectionsPagerAdapter = new SectionsPagerAdapter(PlaylistActivity.this, getSupportFragmentManager(),2);
             sectionsPagerAdapter.addFragment(new CurrentPlaylist(myBinder,playlistArray[0],playlistArray[1],helper),"CURRENT");
             sectionsPagerAdapter.addFragment(new FavouriteActivity(myBinder,playlistArray[1]),"FAVOURITES");
             ViewPager viewPager = findViewById(R.id.view_pager);
             viewPager.setAdapter(sectionsPagerAdapter);
             TabLayout tabs = findViewById(R.id.tabs);
             tabs.setupWithViewPager(viewPager);
             controllerFragment = (ControllerFragment)getSupportFragmentManager().findFragmentById(R.id.controllerfragment);
             controllerFragment.setBinder(myBinder);

         }

         @Override
         public void onServiceDisconnected(ComponentName componentName) {
             mBound = false;
         }
     };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        if(!mBound){
            Intent i = new Intent(this,MyService.class);
            bindService(i,serviceConnection,BIND_AUTO_CREATE);
        }
        helper = new PlaylistDatabaseHelper(this);

    }



    Bitmap getAlbumArt(Uri uri){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(this,uri);

        byte[] data = mmr.getEmbeddedPicture();
        if(data!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
            return bitmap;
        }
        else{
            return BitmapFactory.decodeResource(getResources(),R.drawable.default1);
        }

    }


    public Playlist loadFavouritePlaylist(){
        Cursor cursor = helper.retrieveAll(PlaylistDatabaseHelper.PLAYLIST_2);
        Playlist playlist = new Playlist("Current");

        if(cursor.getCount()!=0){
            while(cursor.moveToNext()){
                String name = cursor.getString(1);
                Uri uri= Uri.parse(cursor.getString(2));
                int isLiked = cursor.getInt(3);
                Bitmap bitmap = getAlbumArt(uri);
                Song song = new Song(uri,bitmap,name);
                playlist.addNewSong(song);
            }
        }
        return playlist;
    }


    /*@Override
    protected void onStart() {
        super.onStart();
        uihandler = new Handler();
        ui = new Runnable() {
            @Override
            public void run() {
                if(mBound){

                }

            }
        };
    }*/


    void bindAudioService(){
        if(!mBound){
            Intent i = new Intent(this,MyService.class);
            bindService(i,serviceConnection,BIND_AUTO_CREATE);
        }
    }
    void unbindAudioService(){
        if(mBound){
            unbindService(serviceConnection);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindAudioService();
        helper.close();
    }


}
