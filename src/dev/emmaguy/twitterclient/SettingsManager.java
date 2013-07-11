package dev.emmaguy.twitterclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsManager implements IContainSettings {
    
    private SharedPreferences sharedPreferences;
    
    private static final String OAUTH_TOKEN = "oauthtoken";
    private static final String OAUTH_VERIFIER = "oauthveri";
    
    private static final String USER_TOKEN = "userToken";
    private static final String USER_SECRET = "userSecret";

    private static final String USERNAME = "username";
    private static final String USER_ID = "userId";

    private static final String MAX_ID = "maxId";
    private static final String SINCE_ID = "sinceId";
    private static final String BOTTOM_OF_GAP_ID = "bottomGap";

    private static final String TWEET_POSITION = "tweetPos";

    public SettingsManager(Context context) {
	sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public int getTweetPosition() {
	return sharedPreferences.getInt(TWEET_POSITION, 0);
    }

    @Override
    public void setTweetPosition(int position) {
	SharedPreferences.Editor editor = sharedPreferences.edit();
	editor.putInt(TWEET_POSITION, position);
	editor.commit();
    }

    @Override
    public long getTweetBottomOfGapId() {
	return sharedPreferences.getLong(BOTTOM_OF_GAP_ID, 0);
    }

    @Override
    public void setTweetBottomOfGapId(long oldestTweetId) {
	SharedPreferences.Editor editor = sharedPreferences.edit();
	editor.putLong(BOTTOM_OF_GAP_ID, oldestTweetId);
	editor.commit();
    }

    public long getTweetSinceId() {
	return sharedPreferences.getLong(SINCE_ID, 0);
    }

    public void setTweetSinceId(long sinceId) {
	SharedPreferences.Editor editor = sharedPreferences.edit();
	editor.putLong(SINCE_ID, sinceId);
	editor.commit();
    }

    public long getTweetMaxId() {
	return sharedPreferences.getLong(MAX_ID, 0);
    }

    public void setTweetMaxId(long maxId) {
	SharedPreferences.Editor editor = sharedPreferences.edit();
	editor.putLong(MAX_ID, maxId);
	editor.commit();
    }

    private boolean IsNullOrEmpty(String s) {
	return s == null || s.length() == 0;
    }

    public boolean credentialsAvailable() {
	return !IsNullOrEmpty(getUserToken()) && !IsNullOrEmpty(getUserTokenSecret()) && !IsNullOrEmpty(getUsername()) && getUserId() >= 0;
    }

    public void saveTokenAndSecret(String token, String tokenSecret) {
	SharedPreferences.Editor editor = sharedPreferences.edit();
	editor.putString(OAUTH_TOKEN, token);
	editor.putString(OAUTH_VERIFIER, tokenSecret);
	editor.commit();
    }

    public void saveUserTokenAndSecret(String token, String secret) {
	SharedPreferences.Editor editor = sharedPreferences.edit();
	editor.putString(USER_TOKEN, token);
	editor.putString(USER_SECRET, secret);
	editor.commit();
    }

    public void saveUsernameAndUserId(String userName, long userId) {
	SharedPreferences.Editor editor = sharedPreferences.edit();
	editor.putString(USERNAME, userName);
	editor.putLong(USER_ID, userId);
	editor.commit();
    }

    public String getToken() {
	return sharedPreferences.getString(OAUTH_TOKEN, null);
    }

    public String getTokenSecret() {
	return sharedPreferences.getString(OAUTH_VERIFIER, null);
    }

    public String getUserToken() {
	return sharedPreferences.getString(USER_TOKEN, null);
    }

    public String getUserTokenSecret() {
	return sharedPreferences.getString(USER_SECRET, null);
    }

    public String getUsername() {
	return sharedPreferences.getString(USERNAME, null);
    }

    private long getUserId() {
	return sharedPreferences.getLong(USER_ID, -1);
    }

    public void clearTweetPositions() {
	setTweetSinceId(-1);
	setTweetMaxId(-1);
    }
}
