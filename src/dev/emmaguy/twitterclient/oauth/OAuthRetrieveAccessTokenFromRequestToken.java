package dev.emmaguy.twitterclient.oauth;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import dev.emmaguy.twitterclient.ConsumerInfo;
import dev.emmaguy.twitterclient.IContainSettings;
import dev.emmaguy.twitterclient.ui.OnAccessTokenRetrievedListener;

public class OAuthRetrieveAccessTokenFromRequestToken extends AsyncTask<Uri, Void, Void> {

    private final IContainSettings settings;
    private final RequestToken requestToken;
    private final String verifier;
    private final OnAccessTokenRetrievedListener listener;

    public OAuthRetrieveAccessTokenFromRequestToken(final IContainSettings settings, final RequestToken requestToken,
	    final String verifier, final OnAccessTokenRetrievedListener listener) {
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
	    Log.i("retrieve access token",
		    "token: " + requestToken.getToken() + " secret: " + requestToken.getTokenSecret());

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
	listener.onRetrievedAccessToken();
    }
}
