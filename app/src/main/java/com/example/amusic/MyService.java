package com.example.amusic;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyService extends Service {
    MyBinder myBinder ;

    public MyService(){
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(myBinder == null)
            myBinder = new MyBinder(this);
        return myBinder;
    }
}
