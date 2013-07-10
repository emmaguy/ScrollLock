package dev.emmaguy.twitterclient.oauth;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import dev.emmaguy.twitterclient.ConsumerInfo;
import dev.emmaguy.twitterclient.ui.SignInFragment;

public class OAuthObtainRequestTokenAndRedirectToBrowser extends AsyncTask<Object, Void, RequestToken> {
    
    private final Activity activity;
    private final RequestTokenReceivedListener listener;
    
    public OAuthObtainRequestTokenAndRedirectToBrowser(final Activity activity, final RequestTokenReceivedListener listener) {
	this.activity = activity;
	this.listener = listener;
    } 

    @Override
    protected RequestToken doInBackground(Object... objects) {
	RequestToken requestToken = null;
	try {
   
	    Twitter twitter = TwitterFactory.getSingleton();
	    twitter.setOAuthConsumer(ConsumerInfo.CONSUMER_KEY, ConsumerInfo.CONSUMER_SECRET);

	    requestToken = twitter.getOAuthRequestToken(SignInFragment.SCROLLLOCK_CALLBACK);
	    Log.i("request token", "token: " + requestToken.getToken() + " secret: " + requestToken.getTokenSecret());
	    
	    
	    redirectToBrowser(Uri.parse(requestToken.getAuthenticationURL()));

	} catch (TwitterException te) {
	    Log.e("ScrollLock", "Failed to get request token: " + te.getMessage(), te);
	}

	return requestToken;
    }
    
    public interface RequestTokenReceivedListener {
	void onRequestTokenReceived(RequestToken r);
    }

    @Override
    protected void onPostExecute(RequestToken r) {
	listener.onRequestTokenReceived(r);
    }
    
    private void redirectToBrowser(Uri uri) {
	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	activity.startActivity(intent);
    }
}