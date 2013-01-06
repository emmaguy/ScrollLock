package com.eguy.twitterapi;

import org.json.JSONException;

import android.content.ContentValues;

import com.eguy.db.TweetProvider;

public class TweetBuilder
{
	private JsonTweet tweet;

	public TweetBuilder(JsonTweet tweet)
	{
		this.tweet = tweet;
	}
	
	public ContentValues build() throws JSONException
	{
		ContentValues tweetValue = new ContentValues();
		tweetValue.put(TweetProvider.TWEET_ID, tweet.getTweetId());
		tweetValue.put(TweetProvider.TWEET_TEXT, tweet.getText());
		tweetValue.put(TweetProvider.TWEET_CREATED_AT, tweet.getTweetCreatedAt());
		tweetValue.put(TweetProvider.TWEET_USER_ID, tweet.getUserId());
		tweetValue.put(TweetProvider.TWEET_USERNAME, tweet.getUsername());
		tweetValue.put(TweetProvider.TWEET_PROFILE_PIC_URL, tweet.getProfilePicUrl());
		return tweetValue;
	}
}
