package com.eguy.twitterapi;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.eguy.SettingsManager;
import com.eguy.db.TweetProvider;
import com.eguy.oauth.OAuthProviderAndConsumer;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoadTweetsAndUpdateDbTask extends AsyncTask<Void, Void, JSONArray>
{
	private static final int MAX_NUMBER_OF_REQUESTS_PER_WINDOW = 5;
	private static final int WINDOW_LENGTH = 15;

	private OAuthProviderAndConsumer producerAndConsumer;
	private SettingsManager settingsManager;
	private static RateCalculator rateCalculator;

	private Context context;
	private long sinceId;
	private long maxId;
	private int numberOfTweetsToRequest;
	private boolean getLatestTweets;
	private IRequestTweets requestTweets;

	static
	{
		rateCalculator = new RateCalculator(MAX_NUMBER_OF_REQUESTS_PER_WINDOW, WINDOW_LENGTH);
	}

	public LoadTweetsAndUpdateDbTask(OAuthProviderAndConsumer producerAndConsumer, SettingsManager settingsManager,
			Context context, long sinceId, long maxId, int numberOfTweetsToRequest, boolean getLatestTweets,
			IRequestTweets requestTweets)
	{
		this.producerAndConsumer = producerAndConsumer;
		this.settingsManager = settingsManager;
		this.context = context;
		this.sinceId = sinceId;
		this.maxId = maxId;
		this.numberOfTweetsToRequest = numberOfTweetsToRequest;
		this.getLatestTweets = getLatestTweets;
		this.requestTweets = requestTweets;
	}

	@Override
	protected JSONArray doInBackground(Void... arg0)
	{
		if (!rateCalculator.canMakeRequest())
		{
			Log.d("ScrollLock", "Not performing request to twitter as it may exceed the rate limit");
			return null;
		}

		String uri = new HomeTimelineUriBuilder(settingsManager.getUsername(), numberOfTweetsToRequest, sinceId, maxId,
				getLatestTweets).build();
		HttpClient client = new HttpClientBuilder().build();

		return requestTweets.requestTweets(uri, producerAndConsumer, rateCalculator, client);
	}

	protected void onPostExecute(JSONArray jsonArray)
	{
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

		TimelineAction gapCalculator = new TimelineGapCalculator(oldestTweetId, settingsManager.getTweetMaxId()).calculate();

		// if we have processed a newer tweet than what we already have,
		// store its id
		if (getLatestTweets && newestTweetId > settingsManager.getTweetSinceId() && newestTweetId > 0)
		{
			Log.d("ScrollLock", "Setting since_id to: " + newestTweetId);
			settingsManager.setTweetSinceId(newestTweetId);
		}

		if (getLatestTweets && oldestTweetId > settingsManager.getTweetMaxId() && oldestTweetId > 0)
		{
			Log.d("ScrollLock", "Setting max_id to: " + oldestTweetId);
			settingsManager.setTweetMaxId(oldestTweetId);
		}

		if (gapCalculator.requestMoreTweest())
		{
			Log.d("ScrollLock", "Requesting more tweets");
			new LoadTweetsAndUpdateDbTask(producerAndConsumer, settingsManager, context, gapCalculator.getTopOfGap(),
					gapCalculator.getBottomOfGap(), 1, false, requestTweets).execute();
		}

		context.getContentResolver().bulkInsert(TweetProvider.TWEET_URI, tweets);
		Log.d("ScrollLock", "Leaving req tweets method");
	}
}