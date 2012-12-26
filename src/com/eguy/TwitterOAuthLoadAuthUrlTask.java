package com.eguy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class TwitterOAuthLoadAuthUrlTask extends AsyncTask<Object, Void, Void>
{
    @Override
    protected Void doInBackground(Object... objects)
    {
        Context context = (Context)objects[0];
        OAuthProvider provider = (OAuthProvider)objects[1];
        OAuthConsumer consumer = (OAuthConsumer)objects[2];
        String callbackUrl = (String)objects[3];
        SharedPreferences preferences = (SharedPreferences)objects[4];

        String authUrl = null;
        try
        {
            authUrl = provider.retrieveRequestToken(consumer, callbackUrl);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(OAuth.OAUTH_TOKEN, consumer.getToken());
            editor.putString(OAuth.OAUTH_VERIFIER, consumer.getTokenSecret());
            editor.commit();
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
            Log.e("blah", "OAuthExpectationFailedException", e);
        }
        catch (OAuthCommunicationException e)
        {
            Log.e("blah", "OAuthCommunicationException", e);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        return null;
    }
}