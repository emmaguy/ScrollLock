package com.eguy.twitterapi;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonTweet
{
    private JSONObject tweet;

    public JsonTweet(JSONObject tweet)
    {
        this.tweet = tweet;
    }

    public String getTweetText() throws JSONException
    {
        return tweet.getString("text");
    }

    public String getTweetCreatedAt() throws JSONException
    {
        return tweet.getString("created_at");
    }

    public long getTweetUserId() throws JSONException
    {
        JSONObject user = tweet.getJSONObject("user");
        return user.getLong("id");
    }

    public long getTweetId() throws JSONException
    {
        return tweet.getLong("id");
    }
}