package com.auth0.authorizationdemo.application;

import android.app.Application;

import com.auth0.android.result.Credentials;

public class App extends Application{

    private Credentials mUserCredentials;

    private static App appSingleton;

    public static App getInstance(){
        return appSingleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        appSingleton = this;
    }


    public Credentials getmUserCredentials() {
        return mUserCredentials;
    }

    public void setmUserCredentials(Credentials mUserCredentials) {
        this.mUserCredentials = mUserCredentials;
    }
}
