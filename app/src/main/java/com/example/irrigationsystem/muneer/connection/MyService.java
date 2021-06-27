package com.example.irrigationsystem.muneer.connection;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyService extends Service {
    MyBinder myBinder=new MyBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
    class MyBinder extends Binder{

    }
}
