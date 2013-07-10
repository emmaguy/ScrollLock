package dev.emmaguy.twitterclient.twitterapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import dev.emmaguy.twitterclient.IContainSettings;
import dev.emmaguy.twitterclient.db.IStoreTweets;

public class RequestTweetsAndUpdateDbTask extends AsyncTask<Void, Void, JSONArray> {
    private IRequestTweets requestTweets;
    private IContainSettings settingsManager;
    private IStoreTweets tweetStorer;

    public RequestTweetsAndUpdateDbTask(IContainSettings settingsManager, IStoreTweets tweetStorer,
	    IRequestTweets requestTweets) {
	this.settingsManager = settingsManager;
	this.tweetStorer = tweetStorer;
	this.requestTweets = requestTweets;
    }

    @Override
    protected JSONArray doInBackground(Void... arg0) {
	return requestTweets.requestTweets();
    }

    protected void onPostExecute(JSONArray jsonArray) {
	Log.d("ScrollLock", "In post exe");
	if (jsonArray == null)
	    return;

	long newestTweetId = 0;
	long oldestTweetId = Long.MAX_VALUE;

	ContentValues[] tweets = new ContentValues[jsonArray.length()];
	for (int i = 0; i < jsonArray.length(); ++i) {
	    try {
		JSONObject status = jsonArray.getJSONObject(i);
		JsonTweet tweet = new JsonTweet(status);
		tweets[i] = new TweetBuilder(tweet).build();

		if (tweet.getTweetId() > newestTweetId) {
		    newestTweetId = tweet.getTweetId();
		}
		if (tweet.getTweetId() < oldestTweetId) {
		    oldestTweetId = tweet.getTweetId();
		}
	    } catch (JSONException e) {
		Log.d("ScrollLock", "error: " + e.getMessage());
		Log.e("ScrollLock", e.getClass().toString(), e);
	    }
	}

	Log.d("ScrollLock", "Parsed: " + tweets.length + " tweets, oldestId: " + oldestTweetId);
	if (tweets.length == 0) {
	    Log.d("ScrollLock", "No tweets found");
	    if (settingsManager.getTweetBottomOfGapId() != -1) {
		Log.d("ScrollLock", "Setting max_id to: " + settingsManager.getTweetBottomOfGapId());
		settingsManager.setTweetMaxId(settingsManager.getTweetBottomOfGapId());
	    }
	    return;
	}
	tweetStorer.addTweets(tweets);

	TimelineAction gapCalculator = new TimelineGapCalculator(oldestTweetId, settingsManager.getTweetMaxId())
		.calculate();

	// if we have processed a newer tweet than what we already have, store
	// its id
	if (newestTweetId > settingsManager.getTweetSinceId() && newestTweetId > 0) {
	    Log.d("ScrollLock", "Setting since_id to: " + newestTweetId);
	    settingsManager.setTweetSinceId(newestTweetId);
	}

	if (requestTweets.requestedLatestTweets() && oldestTweetId > settingsManager.getTweetMaxId()
		&& oldestTweetId > 0) {
	    settingsManager.setTweetBottomOfGapId(newestTweetId);
	}

	if (gapCalculator.requestMoreTweets()) {
	    Log.d("ScrollLock", "Requesting more tweets");
	    new RequestTweetsAndUpdateDbTask(settingsManager, tweetStorer, requestTweets.updateRequestToFillGap(
		    gapCalculator.getTopOfGap(), gapCalculator.getBottomOfGap())).execute();
	}
    }
}