package dev.emmaguy.twitterclient.db;

import android.content.ContentResolver;
import android.content.ContentValues;

public class TweetStorer implements IStoreTweets {
    private ContentResolver contentResolver;

    public TweetStorer(ContentResolver contentResolver) {
	this.contentResolver = contentResolver;
    }

    @Override
    public void addTweets(ContentValues[] tweets) {
	contentResolver.bulkInsert(TweetProvider.TWEET_URI, tweets);
    }
}
