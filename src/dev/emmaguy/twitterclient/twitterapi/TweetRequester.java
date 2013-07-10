package dev.emmaguy.twitterclient.twitterapi;

import oauth.signpost.OAuthConsumer;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;

import android.util.Log;

public class TweetRequester implements IRequestTweets {
    private static final int MAX_NUMBER_OF_REQUESTS_PER_WINDOW = 15;
    private static final int WINDOW_LENGTH = 15;

    private static RateCalculator rateCalculator;

    private OAuthConsumer oAuthConsumer;
    private String username;
    private long sinceId;
    private long maxId;
    private int numberOfTweetsToRequest;
    private boolean getLatestTweets;

    static {
	rateCalculator = new RateCalculator(MAX_NUMBER_OF_REQUESTS_PER_WINDOW, WINDOW_LENGTH);
    }

    public TweetRequester(OAuthConsumer consumer, String username, long sinceId, long maxId,
	    int numberOfTweetsToRequest, boolean getLatestTweets) {
	this.oAuthConsumer = consumer;
	this.username = username;
	this.sinceId = sinceId;
	this.maxId = maxId;
	this.numberOfTweetsToRequest = numberOfTweetsToRequest;
	this.getLatestTweets = getLatestTweets;
    }

    public TweetRequester updateRequestToFillGap(long sinceId, long maxId) {
	this.sinceId = sinceId;
	this.maxId = maxId;
	this.getLatestTweets = false;

	return this;
    }

    @Override
    public JSONArray requestTweets() {
	String uri = new HomeTimelineUriBuilder(username, numberOfTweetsToRequest, sinceId, maxId, getLatestTweets)
		.build();
	HttpGet get = new HttpGet(uri);

	try {
	    oAuthConsumer.sign(get);

	    HttpClient client = new HttpClientBuilder().build();

	    if (!rateCalculator.canMakeRequest()) {
		Log.d("ScrollLock", "Not performing request to twitter as it may exceed the rate limit");
		return null;
	    }

	    rateCalculator.requestMade();
	    String response = client.execute(get, new BasicResponseHandler());

	    return new JSONArray(response);
	} catch (org.apache.http.client.HttpResponseException ex) {
	    if (ex.getMessage().contains("Too Many Requests")) {
		Log.e("ScrollLock", "Exceeded twitter rate limit!", ex);
	    }
	} catch (Exception e) {
	    Log.e("ScrollLock", e.getClass().toString(), e);
	}
	return null;
    }

    @Override
    public boolean requestedLatestTweets() {
	return getLatestTweets;
    }
}
