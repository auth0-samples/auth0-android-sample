package auth0.callyourapidemo.application;

import android.app.Application;

import com.auth0.authentication.result.Credentials;

/**
 * Created by emi on 7/19/16.
 */
public class App extends Application {

    Credentials userCredentials;

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
        return userCredentials;
    }

    public void setUserCredentials(Credentials userCredentials) {
        this.userCredentials = userCredentials;
    }
}
