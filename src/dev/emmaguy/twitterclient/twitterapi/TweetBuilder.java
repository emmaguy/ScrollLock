package dev.emmaguy.twitterclient.twitterapi;

import org.json.JSONException;

import twitter4j.Status;
import android.content.ContentValues;
import dev.emmaguy.twitterclient.db.TweetProvider;

public class TweetBuilder {
    private Status tweet;

    public TweetBuilder(Status s) {
	this.tweet = s;
    }

    public ContentValues build() {
	ContentValues tweetValue = new ContentValues();
	tweetValue.put(TweetProvider.TWEET_ID, tweet.getId());
	tweetValue.put(TweetProvider.TWEET_TEXT, tweet.getText());
	tweetValue.put(TweetProvider.TWEET_CREATED_AT, tweet.getCreatedAt().toString());
	tweetValue.put(TweetProvider.TWEET_USER_ID, tweet.getUser().getId());
	tweetValue.put(TweetProvider.TWEET_USERNAME, tweet.getUser().getName());
	tweetValue.put(TweetProvider.TWEET_PROFILE_PIC_URL, tweet.getUser().getOriginalProfileImageURL());
	tweetValue.put(TweetProvider.TWEET_RETWEET_COUNT, tweet.getRetweetCount());

	if (tweet.isRetweet()) {
	    tweetValue.put(TweetProvider.TWEET_RETWEETED_BY_USER_ID, tweet.getRetweetedStatus().getUser().getId());
	    tweetValue.put(TweetProvider.TWEET_RETWEETED_BY_USERNAME, tweet.getRetweetedStatus().getUser().getName());
	    tweetValue.put(TweetProvider.TWEET_RETWEET_PROFILE_PIC_URL, tweet.getRetweetedStatus().getUser().getOriginalProfileImageURL());
	}
	return tweetValue;
    }
}
