package com.example.amusic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PlaylistDatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "playlists.db";

    // Tables
    public static String PLAYLIST_1 = "Current";
    public static String PLAYLIST_2 = "Favourite";

    // Columns for playlist table
    public static String COL_TITLE = "PlaylistTitle";
    public static String COL_NO_SONGS = "NoOfSongs";

    //Columns for PLAYLIST_1
    public static String COL_ID = "ID";
    public static String COL_SONG_TITLE = "SongName";
    public static String COL_SONG_URI = "SongUri";
    public static String COL_IS_LIKED = "LikeFlag";
    String CREATE_CURRENT_TABLE="CREATE TABLE " + PLAYLIST_1 + "(" + COL_ID + " INTEGER PRIMARY KEY," +
            COL_SONG_TITLE+ " VARCHAR," + COL_SONG_URI + " VARCHAR," + COL_IS_LIKED + " INTEGER)";

    String CREATE_FAVOURITE_TABLE="CREATE TABLE " + PLAYLIST_2 + "(" + COL_ID + " INTEGER PRIMARY KEY," +
            COL_SONG_TITLE+ " VARCHAR," + COL_SONG_URI + " VARCHAR," + COL_IS_LIKED + " INTEGER)";

    public PlaylistDatabaseHelper(@Nullable Context context) {
        super(context,DATABASE_NAME,null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v("Database","Db Created");
        sqLiteDatabase.execSQL(CREATE_CURRENT_TABLE);
        sqLiteDatabase.execSQL(CREATE_FAVOURITE_TABLE);

    }


    boolean insertData(String tableName,Song song){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SONG_TITLE,song.getSongName());
        cv.put(COL_SONG_URI,song.getSongUri().toString());
        cv.put(COL_IS_LIKED,song.getIsLiked());

        long flag = db.insert(tableName,null,cv);
        if(flag==-1){
            return false;
        }
        else{
            return true;
        }
    }


    void deleteEntry(String tableName,Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName,COL_ID+"=?",new String[]{id.toString()});

    }

    void deleteSongByName(String tableName,String name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName,COL_SONG_TITLE+"=?",new String[] {name});
    }

    void updateLikeState(String tableName,Integer id,int state){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_IS_LIKED,state);
        db.update(tableName,cv,COL_ID+"=?",new String[]{id.toString()});
    }

    Cursor retrieveAll(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+tableName,null);
        return cursor;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
