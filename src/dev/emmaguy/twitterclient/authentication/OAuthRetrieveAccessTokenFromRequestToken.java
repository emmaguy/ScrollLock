package dev.emmaguy.twitterclient.authentication;

import java.io.PrintWriter;
import java.io.StringWriter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import dev.emmaguy.twitterclient.ConsumerInfo;
import dev.emmaguy.twitterclient.IContainSettings;
import dev.emmaguy.twitterclient.ui.ProgressAsyncTask;

public class OAuthRetrieveAccessTokenFromRequestToken extends ProgressAsyncTask<Uri, Void, Void> {

    private final IContainSettings settings;
    private final RequestToken requestToken;
    private final String verifier;
    private final OnAccessTokenRetrievedListener listener;

    public OAuthRetrieveAccessTokenFromRequestToken(final IContainSettings settings, final RequestToken requestToken,
	    final String verifier, final OnAccessTokenRetrievedListener listener, final Context c, final String dialogMessage) {
	super(c, dialogMessage);
	
	this.settings = settings;
	this.requestToken = requestToken;
	this.verifier = verifier;
	this.listener = listener;
    }

    @Override
    protected Void doInBackground(Uri... params) {
	ConfigurationBuilder builder = new ConfigurationBuilder();
	builder.setOAuthConsumerKey(ConsumerInfo.CONSUMER_KEY);
	builder.setOAuthConsumerSecret(ConsumerInfo.CONSUMER_SECRET);
	Configuration configuration = builder.build();

	TwitterFactory factory = new TwitterFactory(configuration);
	Twitter twitter = factory.getInstance();

	try {
	    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
	    settings.saveUserTokenAndSecret(accessToken.getToken(), accessToken.getTokenSecret());

	    final User user = twitter.verifyCredentials();
	    settings.saveUsernameAndUserId(user.getName(), user.getId());

	} catch (TwitterException te) {
	    Log.e("ScrollLock", "Failed to get request token: " + te.getMessage());

	    StringWriter sw = new StringWriter();
	    te.printStackTrace(new PrintWriter(sw));
	    Log.e("ScrollLock", sw.toString());
	}

	return null;
    }

    @Override
    protected void onPostExecute(Void v) {
	super.onPostExecute(v);
	listener.onRetrievedAccessToken();
    }
}
