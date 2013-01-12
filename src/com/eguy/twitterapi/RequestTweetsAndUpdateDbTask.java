package com.eguy.twitterapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.eguy.IContainSettings;
import com.eguy.db.IStoreTweets;

public class RequestTweetsAndUpdateDbTask extends AsyncTask<Void, Void, JSONArray>
{
	private static final int MAX_NUMBER_OF_REQUESTS_PER_WINDOW = 5;
	private static final int WINDOW_LENGTH = 15;

	private static RateCalculator rateCalculator;

	private IRequestTweets requestTweets;
	private IContainSettings settingsManager;
	private IStoreTweets tweetStorer;

	static
	{
		rateCalculator = new RateCalculator(MAX_NUMBER_OF_REQUESTS_PER_WINDOW, WINDOW_LENGTH);
	}

	public RequestTweetsAndUpdateDbTask(IContainSettings settingsManager, IStoreTweets tweetStorer, IRequestTweets requestTweets)
	{
		this.settingsManager = settingsManager;
		this.tweetStorer = tweetStorer;
		this.requestTweets = requestTweets;
	}

	@Override
	protected JSONArray doInBackground(Void... arg0)
	{
		Log.d("ScrollLock", "Making req...");
		if (!rateCalculator.canMakeRequest())
		{
			Log.d("ScrollLock", "Not performing request to twitter as it may exceed the rate limit");
			return null;
		}

		rateCalculator.requestMade();
		Log.d("ScrollLock", "Totally about to make req...");
		return requestTweets.requestTweets();
	}

	protected void onPostExecute(JSONArray jsonArray)
	{
		Log.d("ScrollLock", "In post exe");
		if (jsonArray == null)
			return;

		long newestTweetId = 0;
		long oldestTweetId = Long.MAX_VALUE;
		ContentValues[] tweets = new ContentValues[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); ++i)
		{
			try
			{
				JSONObject status = jsonArray.getJSONObject(i);
				JsonTweet tweet = new JsonTweet(status);
				tweets[i] = new TweetBuilder(tweet).build();

				if (tweet.getTweetId() > newestTweetId)
				{
					newestTweetId = tweet.getTweetId();
				}
				if (tweet.getTweetId() < oldestTweetId)
				{
					oldestTweetId = tweet.getTweetId();
				}
			}
			catch (JSONException e)
			{
				Log.e("ScrollLock", e.getClass().toString(), e);
			}
		}

		Log.d("ScrollLock", "Parsed: " + tweets.length + " tweets");
		if (tweets.length == 0)
		{
			Log.d("ScrollLock", "No tweets found");
			return;
		}
		tweetStorer.addTweets(tweets);

		TimelineAction gapCalculator = new TimelineGapCalculator(oldestTweetId, settingsManager.getTweetMaxId()).calculate();
		//
		// // if we have processed a newer tweet than what we already have,
		// // store its id
		// if (getLatestTweets && newestTweetId >
		// settingsManager.getTweetSinceId() && newestTweetId > 0)
		// {
		// Log.d("ScrollLock", "Setting since_id to: " + newestTweetId);
		// settingsManager.setTweetSinceId(newestTweetId);
		// }
		//
		// if (getLatestTweets && oldestTweetId >
		// settingsManager.getTweetMaxId() && oldestTweetId > 0)
		// {
		// Log.d("ScrollLock", "Setting max_id to: " + oldestTweetId);
		// settingsManager.setTweetMaxId(oldestTweetId);
		// }

		if (gapCalculator.requestMoreTweets())
		{
			Log.d("ScrollLock", "Requesting more tweets");
			new RequestTweetsAndUpdateDbTask(settingsManager, tweetStorer, requestTweets.updateRequestToFillGap(
					gapCalculator.getTopOfGap(),
					gapCalculator.getBottomOfGap())).execute();
		}
	}
}