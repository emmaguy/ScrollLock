package com.eguy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import oauth.signpost.OAuthConsumer;

public class TimelineActivity extends Activity
{
    private OAuthConsumer consumer;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_activity);

        AuthCredentialManager credentialManager = new AuthCredentialManager(this.getApplicationContext());
        if(!credentialManager.credentialsAvailable())
        {
            startActivity(new Intent(this, AuthenticateActivity.class));
        }
        else
        {
            OAuthProviderAndConsumer producerAndConsumer = new OAuthProviderAndConsumer(credentialManager);
            new TwitterRequests(credentialManager, producerAndConsumer).LoadTweets();
        }
    }
}