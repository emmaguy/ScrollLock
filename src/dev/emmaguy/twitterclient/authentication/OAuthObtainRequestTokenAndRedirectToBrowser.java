package dev.emmaguy.twitterclient.authentication;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import dev.emmaguy.twitterclient.ConsumerInfo;
import dev.emmaguy.twitterclient.ui.ProgressAsyncTask;

public class OAuthObtainRequestTokenAndRedirectToBrowser extends ProgressAsyncTask<Object, Void, RequestToken> {
    
    private final Activity activity;
    private final OnRequestTokenReceivedListener listener;
    
    public OAuthObtainRequestTokenAndRedirectToBrowser(final Activity activity, final OnRequestTokenReceivedListener listener, final String dialogMessage) {
	super(activity, dialogMessage);
	
	this.activity = activity;
	this.listener = listener;
    } 

    @Override
    protected RequestToken doInBackground(Object... objects) {
	RequestToken requestToken = null;
	try {
   
	    ConfigurationBuilder builder = new ConfigurationBuilder();
	    builder.setOAuthConsumerKey(ConsumerInfo.CONSUMER_KEY);
	    builder.setOAuthConsumerSecret(ConsumerInfo.CONSUMER_SECRET);
	    
	    TwitterFactory factory = new TwitterFactory(builder.build());
	    Twitter twitter = factory.getInstance();
	    requestToken = twitter.getOAuthRequestToken(SignInFragment.SCROLLLOCK_CALLBACK);

	    redirectToBrowser(Uri.parse(requestToken.getAuthenticationURL()));

	} catch (TwitterException te) {
	    Log.e("ScrollLock", "Failed to get request token: " + te.getMessage(), te);
	}

	return requestToken;
    }
    
    public interface OnRequestTokenReceivedListener {
	void onRequestTokenReceived(RequestToken r);
    }

    @Override
    protected void onPostExecute(RequestToken r) {
	super.onPostExecute(r);
	listener.onRequestTokenReceived(r);
    }
    
    private void redirectToBrowser(Uri uri) {
	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	activity.startActivity(intent);
    }
}