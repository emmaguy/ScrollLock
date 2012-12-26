package com.eguy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import junit.framework.Assert;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class AuthenticateActivity extends Activity
{
    SharedPreferences preferences;
    OAuthProvider provider = new DefaultOAuthProvider(
            "https://api.twitter.com/oauth/request_token",
            "https://api.twitter.com/oauth/access_token",
            "https://api.twitter.com/oauth/authorize");

    OAuthConsumer consumer;
    final String CALLBACK_URL = "scrolllock://callback";
    private String USER_TOKEN = "userToken";
    private String USER_SECRET = "userSecret";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        preferences = this.getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        final Context context = getApplicationContext();

        String key = context.getString(R.string.consumer_key);
        String secret = context.getString(R.string.consumer_secret);

        consumer = new DefaultOAuthConsumer(key, secret);

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new TwitterOAuthLoadAuthUrlTask().execute(context, provider, consumer, CALLBACK_URL, preferences);
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Uri uri = this.getIntent().getData();
        if (uri != null && uri.toString().startsWith(CALLBACK_URL))
        {
            String token = preferences.getString(OAuth.OAUTH_TOKEN, null);
            String secret = preferences.getString(OAuth.OAUTH_VERIFIER, null);

            consumer.setTokenWithSecret(token, secret);
            try
            {
                String otoken = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
                String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

                Assert.assertEquals(otoken, consumer.getToken());

                provider.retrieveAccessToken(consumer, verifier);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(USER_TOKEN, consumer.getToken());
                editor.putString(USER_SECRET, consumer.getTokenSecret());
                editor.commit();

                startActivity(new Intent(this, TimelineActivity.class));
            }
            catch (OAuthMessageSignerException e)
            {
                Log.e("blah", "OAuthMessageSignerException", e);

            }
            catch (OAuthNotAuthorizedException e)
            {
                Log.e("blah", "OAuthNotAuthorizedException", e);

            }
            catch (OAuthExpectationFailedException e)
            {
                Log.e("blah", "OAuthNotAuthorizedException", e);

            }
            catch (OAuthCommunicationException e)
            {
                Log.e("blah", "OAuthCommunicationException", e);
            }
        }
    }
}
