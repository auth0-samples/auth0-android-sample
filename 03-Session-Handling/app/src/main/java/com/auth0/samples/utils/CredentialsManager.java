package com.auth0.samples.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.auth0.android.result.Credentials;

public class CredentialsManager {

    private static final String PREFERENCES_NAME = "auth0";
    private final static String REFRESH_TOKEN = "refresh_token";
    private final static String ACCESS_TOKEN = "access_token";
    private final static String ID_TOKEN = "id_token";
    private final static String TOKEN_TYPE = "token_type";
    private final static String EXPIRES_IN = "expires_in";

    public static void saveCredentials(Context context, Credentials credentials) {
        SharedPreferences sp = context.getSharedPreferences(
                PREFERENCES_NAME, Context.MODE_PRIVATE);

        sp.edit()
                .putString(ID_TOKEN, credentials.getIdToken())
                .putString(REFRESH_TOKEN, credentials.getRefreshToken())
                .putString(ACCESS_TOKEN, credentials.getAccessToken())
                .putString(TOKEN_TYPE, credentials.getType())
                .putLong(EXPIRES_IN, credentials.getExpiresIn())
                .apply();
    }

    public static Credentials getCredentials(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                PREFERENCES_NAME, Context.MODE_PRIVATE);

        return new Credentials(
                sp.getString(ID_TOKEN, null),
                sp.getString(ACCESS_TOKEN, null),
                sp.getString(TOKEN_TYPE, null),
                sp.getString(REFRESH_TOKEN, null),
                sp.getLong(EXPIRES_IN, 0));
    }

    public static void deleteCredentials(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                PREFERENCES_NAME, Context.MODE_PRIVATE);

        sp.edit()
                .putString(ID_TOKEN, null)
                .putString(REFRESH_TOKEN, null)
                .putString(ACCESS_TOKEN, null)
                .putString(TOKEN_TYPE, null)
                .putLong(EXPIRES_IN, 0)
                .apply();
    }


}