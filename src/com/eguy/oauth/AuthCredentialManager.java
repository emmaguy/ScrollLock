package com.eguy.oauth;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import oauth.signpost.OAuth;

public class AuthCredentialManager
{
    private SharedPreferences sharedPreferences;
    private String USER_TOKEN = "userToken";
    private String USER_SECRET = "userSecret";

    private String USERNAME = "username";
    private String USER_ID = "userId";

    public AuthCredentialManager(Context context)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private boolean IsNullOrEmpty(String s)
    {
        return s == null || s.isEmpty();
    }

    public boolean credentialsAvailable()
    {
        return !IsNullOrEmpty(getUserToken()) && !IsNullOrEmpty(getUserTokenSecret())
                && !IsNullOrEmpty(getUsername()) && !IsNullOrEmpty(getUserId());
    }

    public void clearCredentials()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_TOKEN, "");
        editor.putString(USER_SECRET, "");
        editor.putString(OAuth.OAUTH_TOKEN, "");
        editor.putString(OAuth.OAUTH_VERIFIER, "");
        editor.commit();
    }

    public void saveTokenAndSecret(String token, String tokenSecret)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(OAuth.OAUTH_TOKEN, token);
        editor.putString(OAuth.OAUTH_VERIFIER, tokenSecret);
        editor.commit();
    }

    public void saveUserTokenAndSecret(String token, String secret)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_TOKEN, token);
        editor.putString(USER_SECRET, secret);
        editor.commit();
    }

    public void saveUsernameAndUserId(String userName, String userId)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, userName);
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    public String getToken()
    {
        return sharedPreferences.getString(OAuth.OAUTH_TOKEN, null);
    }

    public String getTokenSecret()
    {
        return sharedPreferences.getString(OAuth.OAUTH_VERIFIER, null);
    }

    public String getUserToken()
    {
        return sharedPreferences.getString(USER_TOKEN, null);
    }

    public String getUserTokenSecret()
    {
        return sharedPreferences.getString(USER_SECRET, null);
    }

    public String getUsername()
    {
        return sharedPreferences.getString(USERNAME, null);
    }

    private String getUserId()
    {
        return sharedPreferences.getString(USER_ID, null);
    }
}
