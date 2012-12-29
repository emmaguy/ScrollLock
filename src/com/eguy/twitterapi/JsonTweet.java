package com.eguy.twitterapi;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonTweet
{
    private JSONObject tweet;
    private JSONObject user;

    public JsonTweet(JSONObject tweet) throws JSONException
    {
        this.tweet = tweet;
        user = tweet.getJSONObject("user");
    }

    public String getText() throws JSONException
    {
        return tweet.getString("text");
    }

    public String getTweetCreatedAt() throws JSONException
    {
        return tweet.getString("created_at");
    }

    public long getTweetId() throws JSONException
    {
        return tweet.getLong("id");
    }

    public long getUserId() throws JSONException
    {
        return user.getLong("id");
    }

    public String getUsername() throws JSONException
    {
        return user.getString("screen_name");
    }

    public String getProfilePicUrl() throws JSONException
    {
        return user.getString("profile_image_url");
    }
}