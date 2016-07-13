package com.auth0.passwordless.email;

import android.app.Application;

import com.auth0.lock.Lock;
import com.auth0.lock.LockProvider;

public class MyApplication extends Application implements LockProvider {

    private Lock lock;

    public void onCreate() {
        super.onCreate();
        lock = new Lock.Builder()
                .loadFromApplication(this)
                .closable(true)
                .build();
    }

    @Override
    public Lock getLock() {
        return lock;
    }
}