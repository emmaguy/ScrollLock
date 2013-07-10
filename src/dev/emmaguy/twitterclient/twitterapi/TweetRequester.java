package dev.emmaguy.twitterclient.twitterapi;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.util.Log;
import dev.emmaguy.twitterclient.ConsumerInfo;
import dev.emmaguy.twitterclient.IContainSettings;

public class TweetRequester implements IRequestTweets {

    private String username;
    private long sinceId;
    private long maxId;
    private int numberOfTweetsToRequest;
    private boolean getLatestTweets;
    private IContainSettings settings;

    public TweetRequester(String username, long sinceId, long maxId,
	    int numberOfTweetsToRequest, boolean getLatestTweets, IContainSettings settings) {
	this.username = username;
	this.sinceId = sinceId;
	this.maxId = maxId;
	this.numberOfTweetsToRequest = numberOfTweetsToRequest;
	this.getLatestTweets = getLatestTweets;
	this.settings = settings;
    }

    public TweetRequester updateRequestToFillGap(long sinceId, long maxId) {
	this.sinceId = sinceId;
	this.maxId = maxId;
	this.getLatestTweets = false;

	return this;
    }

    @Override
    public List<Status> requestTweets() {
	String uri = new HomeTimelineUriBuilder(username, numberOfTweetsToRequest, sinceId, maxId, getLatestTweets)
		.build();
	

	try {
	    Twitter twitter = TwitterFactory.getSingleton();
	    twitter.setOAuthConsumer(ConsumerInfo.CONSUMER_KEY, ConsumerInfo.CONSUMER_SECRET);
	    twitter.setOAuthAccessToken(new AccessToken(settings.getUserToken(), settings.getUserTokenSecret()));
	    List<Status> statuses = twitter.getHomeTimeline();
	    
	    return statuses;
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
