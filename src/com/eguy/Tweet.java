package com.eguy;

import org.json.JSONException;
import org.json.JSONObject;

public class Tweet
{
    private JSONObject tweet;

    public Tweet(JSONObject tweet)
    {
        this.tweet = tweet;
    }

    public String getTweetText() throws JSONException
    {
        return tweet.getString("text");
    }

    public long getTweetId() throws JSONException
    {
        return tweet.getLong("id");
    }

    public String getTweetCreatedAt() throws JSONException
    {
        return tweet.getString("created_at");
    }
}