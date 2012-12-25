package com.eguy;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    OAuthProvider provider = new DefaultOAuthProvider(
            "https://api.twitter.com/oauth/request_token",
            "https://api.twitter.com/oauth/access_token",
            "https://api.twitter.com/oauth/authorize");

    OAuthConsumer consumer;
    final String CALLBACK_URL = "scrolllock://callback";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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
                new TwitterOAuthLoadAuthUrlTask().execute(context, provider, consumer, CALLBACK_URL);
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Uri data = this.getIntent().getData();
        if (data != null && data.toString().startsWith(CALLBACK_URL))
        {
            String token = data.getQueryParameter(OAuth.OAUTH_TOKEN);
            String verifier = data.getQueryParameter(OAuth.OAUTH_VERIFIER);
            try
            {
               // Assert.assertEquals(token, consumer.getToken());

                provider.retrieveAccessToken(consumer, verifier);

                //store consumer.getToken(), consumer.getConsumerSecret()
            } catch (OAuthMessageSignerException e)
            {
                Log.e("blah", "OAuthMessageSignerException", e);

            } catch (OAuthNotAuthorizedException e)
            {
                Log.e("blah", "OAuthNotAuthorizedException", e);

            } catch (OAuthExpectationFailedException e)
            {
                Log.e("blah", "OAuthNotAuthorizedException", e);

            } catch (OAuthCommunicationException e)
            {
                Log.e("blah", "OAuthCommunicationException", e);
            }
        }
    }
}
