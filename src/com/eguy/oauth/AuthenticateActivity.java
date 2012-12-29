package com.eguy.oauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        oAuthProviderAndConsumer = new OAuthProviderAndConsumer(new SettingsManager(this.getApplicationContext()));

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new OAuthObtainRequestTokenAndRedirectToBrowser().execute(context, oAuthProviderAndConsumer);
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
            retrieveAccessTokenFromRequestToken(uri);
        }
    }

    private void retrieveAccessTokenFromRequestToken(Uri uri)
    {
        SettingsManager credentialManager = new SettingsManager(this.getApplicationContext());
        String token = credentialManager.getToken();
        String secret = credentialManager.getTokenSecret();

        OAuthConsumer consumer = oAuthProviderAndConsumer.getConsumer();
        OAuthProvider provider = oAuthProviderAndConsumer.getProvider();

        consumer.setTokenWithSecret(token, secret);
        try
        {
            Assert.assertEquals(uri.getQueryParameter(OAuth.OAUTH_TOKEN), consumer.getToken());

            provider.retrieveAccessToken(consumer, uri.getQueryParameter(OAuth.OAUTH_VERIFIER));
            credentialManager.saveUserTokenAndSecret(consumer.getToken(), consumer.getTokenSecret());

            HttpParameters responseParameters = provider.getResponseParameters();
            String userName = responseParameters.getFirst("screen_name");
            String userId = responseParameters.getFirst("user_id");

            credentialManager.saveUsernameAndUserId(userName, userId);

            startActivity(new Intent(this, TimelineActivity.class));
        }
        catch (Exception e)
        {
            Log.e("ScrollLock", e.getClass().toString(), e);
        }
    }
}