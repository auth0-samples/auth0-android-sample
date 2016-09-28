package com.auth0.profiledemo.application;

import android.app.Application;

import com.auth0.android.result.Credentials;

public class App extends Application {

    private Credentials mUserCredentials;

    private static App appSingleton;

    public static App getInstance() {
        return appSingleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appSingleton = this;
    }

    public Credentials getUserCredentials() {
        return mUserCredentials;
    }

    public void setUserCredentials(Credentials userCredentials) {
        this.mUserCredentials = userCredentials;
    }
}
