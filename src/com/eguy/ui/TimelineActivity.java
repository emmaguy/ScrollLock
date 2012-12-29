package com.eguy.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.eguy.SettingsManager;
import com.eguy.oauth.AuthenticateActivity;
import com.eguy.R;
import com.eguy.db.TweetDatabase;
import com.eguy.oauth.OAuthProviderAndConsumer;
import com.eguy.twitterapi.LoadTweetsAndUpdateDbTask;
import oauth.signpost.OAuthConsumer;

public class TimelineActivity extends Activity
{
    private OAuthConsumer consumer;
    private TweetDatabase tweetDatabase;
    private CursorAdapter cursorAdapter;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timeline_listview);

        tweetDatabase = new TweetDatabase(getApplicationContext());
        //tweetDatabase.refreshDb();

        cursorAdapter = new TimelineAdapter(this, tweetDatabase.getTweetsCursor(), tweetDatabase);
        ((ListView)findViewById(R.id.lstTimeline)).setAdapter(cursorAdapter);

        SettingsManager settingsManager = new SettingsManager(this.getApplicationContext());
        if(!settingsManager.credentialsAvailable())
        {
            startActivity(new Intent(this, AuthenticateActivity.class));
        }
        else
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
        new LoadTweetsAndUpdateDbTask(producerAndConsumer, settingsManager, tweetDatabase).execute();
    }
}