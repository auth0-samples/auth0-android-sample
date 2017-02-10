package com.auth0.callyourapidemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.auth0.android.result.Credentials;

public class CredentialsManager {

    private static final String PREFERENCES_NAME = "auth0";
    private final static String REFRESH_TOKEN = "refresh_token";
    private final static String ACCESS_TOKEN = "access_token";
    private final static String ID_TOKEN = "id_token";
    private final static String TOKEN_TYPE = "token_type";

    public static void saveCredentials(Context context, Credentials credentials) {
        SharedPreferences sp = context.getSharedPreferences(
                PREFERENCES_NAME, Context.MODE_PRIVATE);

        sp.edit()
                .putString(ID_TOKEN, credentials.getIdToken())
                .putString(REFRESH_TOKEN, credentials.getRefreshToken())
                .putString(ACCESS_TOKEN, credentials.getAccessToken())
                .putString(TOKEN_TYPE, credentials.getType())
                .apply();
    }

    public static Credentials getCredentials(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                PREFERENCES_NAME, Context.MODE_PRIVATE);

        return new Credentials(
                sp.getString(ID_TOKEN, null),
                sp.getString(ACCESS_TOKEN, null),
                sp.getString(TOKEN_TYPE, null),
                sp.getString(REFRESH_TOKEN, null));
    }

    public static void deleteCredentials(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                PREFERENCES_NAME, Context.MODE_PRIVATE);

        sp.edit()
                .putString(ID_TOKEN, null)
                .putString(REFRESH_TOKEN, null)
                .putString(ACCESS_TOKEN, null)
                .putString(TOKEN_TYPE, null)
                .apply();
    }


}
