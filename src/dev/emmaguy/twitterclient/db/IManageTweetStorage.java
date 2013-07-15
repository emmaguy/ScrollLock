package dev.emmaguy.twitterclient.db;

import android.content.ContentValues;
import android.widget.ListAdapter;

public interface IManageTweetStorage {
    void addTweets(ContentValues[] tweets);
    ListAdapter getAdapter();
}
