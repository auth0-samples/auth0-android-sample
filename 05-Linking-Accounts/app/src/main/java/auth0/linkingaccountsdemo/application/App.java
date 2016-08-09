package auth0.linkingaccountsdemo.application;

import android.app.Application;

import com.auth0.android.result.Credentials;

public class App extends Application{

    Credentials userCredentials;

    private static App appSingleton;

    public static App getInstance(){
        return appSingleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        appSingleton = this;
    }


    public Credentials getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(Credentials userCredentials) {
        this.userCredentials = userCredentials;
    }
}
