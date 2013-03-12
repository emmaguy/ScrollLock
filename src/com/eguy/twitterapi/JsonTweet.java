package com.eguy.twitterapi;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonTweet
{
    private JSONObject tweet;
    private JSONObject user;
    private JSONObject retweetedTweet;
    private JSONObject retweetedUser;

    public JsonTweet(JSONObject tweet) throws JSONException
    {
        this.tweet = tweet;
        user = tweet.getJSONObject("user");
        
        if(tweet.has("retweeted_status"))
    	{
        	retweetedTweet = tweet.getJSONObject("retweeted_status");
        	retweetedUser = tweet.getJSONObject("retweeted_status").getJSONObject("user");
    	}
    }

    public String getText() throws JSONException
    {
    	if(isRetweet())
    	{
    		return retweetedTweet.getString("text");
    	}
    	
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

	public boolean isRetweet()
	{
    	return retweetedUser != null;
	}

	public int getRetweetCount() throws JSONException
	{
		return tweet.getInt("retweet_count");
	}

	public String getRetweetUserId() throws JSONException
	{
		return retweetedUser.getString("id");
	}

	public String getRetweetUsername()  throws JSONException
	{
		return retweetedUser.getString("screen_name");    		
	}
	
	public String getRetweetUserProfileUrl() throws JSONException
	{
		return retweetedUser.getString("profile_image_url");
	}
}