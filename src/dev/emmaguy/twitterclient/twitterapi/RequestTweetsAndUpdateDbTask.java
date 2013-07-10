package dev.emmaguy.twitterclient.twitterapi;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Status;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import dev.emmaguy.twitterclient.IContainSettings;
import dev.emmaguy.twitterclient.db.IStoreTweets;

public class RequestTweetsAndUpdateDbTask extends AsyncTask<Void, Void, List<Status>> {
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
    protected List<twitter4j.Status> doInBackground(Void... arg0) {
	return requestTweets.requestTweets();
    }

    protected void onPostExecute(List<twitter4j.Status> statuses) {
//	long newestTweetId = 0;
//	long oldestTweetId = Long.MAX_VALUE;
	
	if(statuses.size() <= 0){
	    Log.d("ScrollLock", "Found no tweets");
	    return;
	}

	ContentValues[] tweets = new ContentValues[statuses.size()];
	for (int i = 0; i < statuses.size(); ++i) {
//	    try {
		twitter4j.Status s = statuses.get(i);
		tweets[i] = new TweetBuilder(s).build();

//		if (tweet.getTweetId() > newestTweetId) {
//		    newestTweetId = tweet.getTweetId();
//		}
//		if (tweet.getTweetId() < oldestTweetId) {
//		    oldestTweetId = tweet.getTweetId();
//		}
//	    } catch (JSONException e) {
//		Log.d("ScrollLock", "error: " + e.getMessage());
//		Log.e("ScrollLock", e.getClass().toString(), e);
//	    }
	}

//	Log.d("ScrollLock", "Parsed: " + tweets.length + " tweets, oldestId: " + oldestTweetId);
//	if (tweets.length == 0) {
//	    Log.d("ScrollLock", "No tweets found");
//	    if (settingsManager.getTweetBottomOfGapId() != -1) {
//		Log.d("ScrollLock", "Setting max_id to: " + settingsManager.getTweetBottomOfGapId());
//		settingsManager.setTweetMaxId(settingsManager.getTweetBottomOfGapId());
//	    }
//	    return;
//	}
	tweetStorer.addTweets(tweets);
//
//	TimelineAction gapCalculator = new TimelineGapCalculator(oldestTweetId, settingsManager.getTweetMaxId())
//		.calculate();
//
//	// if we have processed a newer tweet than what we already have, store
//	// its id
//	if (newestTweetId > settingsManager.getTweetSinceId() && newestTweetId > 0) {
//	    Log.d("ScrollLock", "Setting since_id to: " + newestTweetId);
//	    settingsManager.setTweetSinceId(newestTweetId);
//	}
//
//	if (requestTweets.requestedLatestTweets() && oldestTweetId > settingsManager.getTweetMaxId()
//		&& oldestTweetId > 0) {
//	    settingsManager.setTweetBottomOfGapId(newestTweetId);
//	}
//
//	if (gapCalculator.requestMoreTweets()) {
//	    Log.d("ScrollLock", "Requesting more tweets");
//	    new RequestTweetsAndUpdateDbTask(settingsManager, tweetStorer, requestTweets.updateRequestToFillGap(
//		    gapCalculator.getTopOfGap(), gapCalculator.getBottomOfGap())).execute();
//	}
    }
}