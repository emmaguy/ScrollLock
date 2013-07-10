package dev.emmaguy.twitterclient.oauth;

import junit.framework.Assert;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.http.HttpParameters;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import dev.emmaguy.twitterclient.SettingsManager;
import dev.emmaguy.twitterclient.ui.TimelineActivity;

public class OAuthRetrieveAccessTokenFromRequestToken extends AsyncTask<Uri, Void, Void> {
    private Context context;

    public OAuthRetrieveAccessTokenFromRequestToken(Context context) {
	this.context = context;
    }

    @Override
    protected Void doInBackground(Uri... params) {
	Uri uri = (Uri) params[0];

	SettingsManager settingsManager = new SettingsManager(context);
	OAuthProviderAndConsumer oAuthProviderAndConsumer = new OAuthProviderAndConsumer(settingsManager);

	String token = settingsManager.getToken();
	String secret = settingsManager.getTokenSecret();

	OAuthConsumer consumer = oAuthProviderAndConsumer.getConsumer();
	OAuthProvider provider = oAuthProviderAndConsumer.getProvider();

	consumer.setTokenWithSecret(token, secret);
	try {
	    Assert.assertEquals(uri.getQueryParameter(OAuth.OAUTH_TOKEN), consumer.getToken());

	    provider.retrieveAccessToken(consumer, uri.getQueryParameter(OAuth.OAUTH_VERIFIER));
	    settingsManager.saveUserTokenAndSecret(consumer.getToken(), consumer.getTokenSecret());

	    HttpParameters responseParameters = provider.getResponseParameters();
	    String userName = responseParameters.getFirst("screen_name");
	    String userId = responseParameters.getFirst("user_id");

	    settingsManager.saveUsernameAndUserId(userName, userId);

	    Intent intent = new Intent(context, TimelineActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    context.startActivity(intent);
	} catch (Exception e) {
	    Log.e("ScrollLock", e.getClass().toString(), e);
	}

	return null;
    }
}
