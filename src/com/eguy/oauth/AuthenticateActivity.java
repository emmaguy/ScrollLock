package com.eguy.oauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.eguy.IContainSettings;
import com.eguy.R;
import com.eguy.SettingsManager;
import com.eguy.ui.TimelineActivity;
import junit.framework.Assert;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.http.HttpParameters;

public class AuthenticateActivity extends Activity
{
    private OAuthProviderAndConsumer oAuthProviderAndConsumer;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Context context = getApplicationContext();
        final IContainSettings settingsManager = new SettingsManager(this.getApplicationContext());
		
        oAuthProviderAndConsumer = new OAuthProviderAndConsumer(settingsManager);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new OAuthObtainRequestTokenAndRedirectToBrowser(settingsManager).execute(context, oAuthProviderAndConsumer);
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Uri uri = this.getIntent().getData();
        if (uri != null && uri.toString().startsWith(oAuthProviderAndConsumer.CALLBACK_URL))
        {
            new OAuthRetrieveAccessTokenFromRequestToken(this.getApplicationContext()).execute(uri);
        }
    }
}