package dev.emmaguy.twitterclient.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ListAdapter;
import dev.emmaguy.twitterclient.timeline.TimelineAdapter;

public class TweetStorer implements IManageTweetStorage, LoaderManager.LoaderCallbacks<Cursor>{
    private ContentResolver contentResolver;
    private TimelineAdapter adapter;
    private Uri uri;
    private Context context;
    private static int loaderid = 0;

    public TweetStorer(ContentResolver contentResolver, LoaderManager loaderManager, TimelineAdapter adapter, Uri uri, Context context) {
	this.contentResolver = contentResolver;
	this.adapter = adapter;
	this.uri = uri;
	this.context = context;
	
	loaderManager.initLoader(loaderid++, null, this);
    }

    @Override
    public void addTweets(ContentValues[] tweets) {
	contentResolver.bulkInsert(uri, tweets);
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
	String[] projection = { TweetProvider.TWEET_TEXT };
	return new CursorLoader(context, uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
	adapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
	adapter.changeCursor(null);
    }

    @Override
    public ListAdapter getAdapter() {
	return adapter;
    }
}
