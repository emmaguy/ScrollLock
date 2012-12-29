package com.eguy.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.eguy.R;
import com.eguy.SettingsManager;
import com.eguy.db.TweetProvider;
import com.eguy.oauth.AuthenticateActivity;
import com.eguy.oauth.OAuthProviderAndConsumer;
import com.eguy.twitterapi.LoadTweetsAndUpdateDbTask;
import oauth.signpost.OAuthConsumer;

public class TimelineActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>
{
    private OAuthConsumer consumer;
    private TweetProvider tweetProvider;
    private CursorAdapter adapter;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timeline_listview);

        getLoaderManager().initLoader(0, null, this);
        adapter = new TimelineAdapter(this, null);
        ((ListView) findViewById(R.id.lstTimeline)).setAdapter(adapter);

        SettingsManager settingsManager = new SettingsManager(this.getApplicationContext());
        if (!settingsManager.credentialsAvailable())
        {
            startActivity(new Intent(this, AuthenticateActivity.class));
        } else
        {
            getLatestTweets();
        }

        initialiseRefreshBar();
    }

    private void initialiseRefreshBar()
    {
        Button btnLogin = (Button) findViewById(R.id.refreshBar);
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getApplicationContext(), "refreshing...", Toast.LENGTH_SHORT).show();
                getLatestTweets();
            }
        });
    }

    private void getLatestTweets()
    {
        SettingsManager settingsManager = new SettingsManager(this.getApplicationContext());
        OAuthProviderAndConsumer producerAndConsumer = new OAuthProviderAndConsumer(settingsManager);
        new LoadTweetsAndUpdateDbTask(producerAndConsumer, settingsManager, this.getApplicationContext()).execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        String[] projection = { TweetProvider.TWEET_TEXT };
        return new CursorLoader(this, TweetProvider.TWEET_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        adapter.swapCursor(null);
    }
}