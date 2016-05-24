package com.jdelorenzo.capstoneproject;

import android.app.Application;

import com.firebase.client.Firebase;

/*
Extension of {@link android.app.Application} for setting up Firebase.
 */
public class MyApplication extends Application {
    private static MyApplication _application;

    public MyApplication getInstance(){
        return _application;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        _application = this;
        Firebase.setAndroidContext(this);
    }
}
