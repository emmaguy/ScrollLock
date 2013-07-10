package dev.emmaguy.twitterclient.twitterapi;

import android.net.Uri;
import android.util.Log;

public class HomeTimelineUriBuilder {
    private final String HOME_TIMELINE_URL = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    private String screenName;
    private final int numberOfTweetsToRequest;
    private final long sinceId;
    private final long maxId;
    private final boolean getLatestTweets;

    public HomeTimelineUriBuilder(String screenName, int numberOfTweetsToRequest, long sinceId, long maxId,
	    boolean getLatestTweets) {
	this.screenName = screenName;
	this.numberOfTweetsToRequest = numberOfTweetsToRequest;
	this.sinceId = sinceId;
	this.maxId = maxId;
	this.getLatestTweets = getLatestTweets;
    }

    public String build() {
	Uri sUri = Uri.parse(HOME_TIMELINE_URL);
	Uri.Builder builder = sUri.buildUpon();
	builder.appendQueryParameter("screen_name", screenName);
	builder.appendQueryParameter("count", String.valueOf(numberOfTweetsToRequest));

	if (!isFirstRequest()) {
	    builder.appendQueryParameter("since_id", String.valueOf(sinceId));
	    Log.d("ScrollLock", "Requesting since_id: " + sinceId);
	}

	if (!getLatestTweets && isMaxIdValid()) {
	    builder.appendQueryParameter("max_id", String.valueOf(maxId));
	    Log.d("ScrollLock", "Requesting max_id: " + maxId);
	}

	return builder.build().toString();
    }

    private boolean isMaxIdValid() {
	return maxId != 0;
    }

    private boolean isFirstRequest() {
	return sinceId == 0;
    }
}
