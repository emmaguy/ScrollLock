package dev.emmaguy.twitterclient.timeline;

import android.content.ContentValues;

public class TimelineUpdate {
    private ContentValues[] tweets;
    private long newestTweetId;
    private long oldestTweetId;

    public TimelineUpdate(ContentValues[] tweets, long newestTweetId, long oldestTweetId) {
	this.tweets = tweets;
	this.newestTweetId = newestTweetId;
	this.oldestTweetId = oldestTweetId;
    }

    public ContentValues[] getTweets() {
	return tweets;
    }

    public long getNewestTweetId() {
	return newestTweetId;
    }

    public long getOldestTweetId() {
	return oldestTweetId;
    }
}
