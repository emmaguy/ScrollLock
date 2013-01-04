package com.eguy.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.eguy.R;
import com.eguy.SettingsManager;
import com.eguy.db.TweetProvider;
import com.eguy.oauth.AuthenticateActivity;
import com.eguy.oauth.OAuthProviderAndConsumer;
import com.eguy.twitterapi.FakeTweetInserterTask;
import com.eguy.twitterapi.LoadTweetsAndUpdateDbTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimelineActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>
{
    private CursorAdapter adapter;
    private int previousNumberOfItemsInList = 0;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timeline_listview);

        getLoaderManager().initLoader(0, null, this);
        adapter = new TimelineAdapter(this, null);
        ((ListView) findViewById(R.id.lstTimeline)).setAdapter(adapter);

        getLatestTweetsOrAuthenticate();
        initialiseRefreshBar();
        initialiseLongClickToShare();
    }

    private void initialiseLongClickToShare()
    {
        final Context context = this;

        ListView timeline = (ListView) findViewById(R.id.lstTimeline);
        timeline.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                ViewHolder h = (ViewHolder)view.getTag();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, h.TweetText.getText());
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent, "Share"));
                return true;
            }
        });

    }

    private void getLatestTweetsOrAuthenticate()
    {
        SettingsManager settingsManager = new SettingsManager(this.getApplicationContext());
        if (settingsManager.credentialsAvailable())
        {
            //getLatestTweets();
        }
        else
        {
            startActivity(new Intent(this, AuthenticateActivity.class));
        }
    }

    private void initialiseRefreshBar()
    {
        final Context context = this;

        Button btnLogin = (Button) findViewById(R.id.refreshBar);
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getApplicationContext(), "refreshing...", Toast.LENGTH_SHORT).show();
                getLatestTweets();
                //new FakeTweetInserterTask(context, new SettingsManager(context)).execute();
            }
        });
    }

    private void getLatestTweets()
    {
        SettingsManager settingsManager = new SettingsManager(this.getApplicationContext());
        OAuthProviderAndConsumer producerAndConsumer = new OAuthProviderAndConsumer(settingsManager);

        Log.d("ScrollLock", "starting run");
        new LoadTweetsAndUpdateDbTask(producerAndConsumer, settingsManager, this.getApplicationContext(), settingsManager.getTweetSinceId(), 0, 20).execute();
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
        ListView timeline = (ListView) findViewById(R.id.lstTimeline);

        int selection = timeline.getFirstVisiblePosition();
        adapter.changeCursor(cursor);
        if(previousNumberOfItemsInList != 0)
        {
            timeline.setSelection(selection + (cursor.getCount() - previousNumberOfItemsInList));
        }
        previousNumberOfItemsInList = cursor.getCount();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        adapter.changeCursor(null);
    }
}