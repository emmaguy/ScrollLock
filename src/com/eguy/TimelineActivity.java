package com.eguy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import com.eguy.db.SavedTweet;
import com.eguy.db.TweetDatabase;
import oauth.signpost.OAuthConsumer;

import java.util.List;

public class TimelineActivity extends Activity
{
    private OAuthConsumer consumer;
    private TweetDatabase tweetDatabase;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timeline_activity);

        tweetDatabase = new TweetDatabase(getApplicationContext());
        tweetDatabase.refreshDb();

        AuthCredentialManager credentialManager = new AuthCredentialManager(this.getApplicationContext());
        if(!credentialManager.credentialsAvailable())
        {
            startActivity(new Intent(this, AuthenticateActivity.class));
        }
        else
        {
            getLatestTweets();
        }

        Button btnLogin = (Button) findViewById(R.id.refreshBar);
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getLatestTweets();
            }
        });
    }

    private void getLatestTweets()
    {
        AuthCredentialManager credentialManager = new AuthCredentialManager(this.getApplicationContext());
        OAuthProviderAndConsumer producerAndConsumer = new OAuthProviderAndConsumer(credentialManager);
        new LoadTweetsAndUpdateDbTask(producerAndConsumer, credentialManager, tweetDatabase, this).execute();
    }

    public void refreshListView()
    {
        List<SavedTweet> tweets = tweetDatabase.getAllSavedTweets();

        ListView lstTimeline = (ListView)findViewById(R.id.lstTimeline);
        lstTimeline.setAdapter(new TimelineAdapater(this, R.layout.tweet_timeline, tweets));
    }
}