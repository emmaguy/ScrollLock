package dev.emmaguy.twitterclient.db;

import android.content.ContentValues;

public interface IStoreTweets {
    void addTweets(ContentValues[] tweets);
}
