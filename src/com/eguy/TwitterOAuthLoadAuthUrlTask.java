package com.eguy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class TwitterOAuthLoadAuthUrlTask extends AsyncTask<Context, Void, Void>
{
    OAuthProvider provider = new DefaultOAuthProvider(
            "https://api.twitter.com/oauth/request_token",
            "https://api.twitter.com/oauth/access_token",
            "https://api.twitter.com/oauth/authorize");

    OAuthConsumer consumer;
    final String CALLBACK_URL = "ScrollLock://callback";

    @Override
    protected Void doInBackground(Context... contexts)
    {
        Context context = contexts[0];

        String key = context.getString(R.string.consumer_key);
        String secret = context.getString(R.string.consumer_secret);

        consumer = new DefaultOAuthConsumer(key, secret);

        String authUrl = null;
        try
        {
            authUrl = provider.retrieveRequestToken(consumer, CALLBACK_URL);
        } catch (OAuthMessageSignerException e)
        {
            Log.e("blah", "OAuthMessageSignerException", e);
        } catch (OAuthNotAuthorizedException e)
        {
            Log.e("blah", "OAuthNotAuthorizedException", e);
        } catch (OAuthExpectationFailedException e)
        {
            Log.e("blah", "OAuthExpectationFailedException", e);
        } catch (OAuthCommunicationException e)
        {
            Log.e("blah", "OAuthCommunicationException", e);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        return null;
    }
}
