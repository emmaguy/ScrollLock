package com.eguy.oauth;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.eguy.IContainSettings;

public class OAuthObtainRequestTokenAndRedirectToBrowser extends AsyncTask<Object, Void, Void>
{
	private IContainSettings settingsManager;

	public OAuthObtainRequestTokenAndRedirectToBrowser(IContainSettings settingsManager)
	{
		this.settingsManager = settingsManager;
	}

	@Override
    protected Void doInBackground(Object... objects)
    {
        Context context = (Context)objects[0];
        OAuthProviderAndConsumer oAuthProviderAndConsumer = (OAuthProviderAndConsumer)objects[1];

        String authUrl = obtainRequestToken(oAuthProviderAndConsumer);
        redirectToBrowser(context, authUrl);

        return null;
    }

    private void redirectToBrowser(Context context, String authUrl)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private String obtainRequestToken(OAuthProviderAndConsumer oAuthProviderAndConsumer)
    {
        String authUrl = null;
        try
        {
            OAuthProvider provider = oAuthProviderAndConsumer.getProvider();
            OAuthConsumer consumer = oAuthProviderAndConsumer.getConsumer();

            authUrl = provider.retrieveRequestToken(consumer, oAuthProviderAndConsumer.CALLBACK_URL);

            settingsManager.saveTokenAndSecret(consumer.getToken(), consumer.getTokenSecret());
        }
        catch (Exception e)
        {
            Log.e("ScrollLock", e.getClass().toString(), e);
        }
        return authUrl;
    }
}