package tests.integration;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import tests.helpers.MockSettingsManager;
import tests.helpers.MockTweetRequester;

import com.eguy.twitterapi.JsonTweet;
import com.eguy.twitterapi.TimelineAction;
import com.eguy.twitterapi.TimelineGapCalculator;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class RequestTweetsAndUpdateDbTaskTest
{
	@Test
	public void testWhen_requesting_latest_tweets_and_timeline_has_a_gap()
	{
		// request latest 5 tweets, newer than id 10
		MockTweetRequester requestTweets = new MockTweetRequester(5, 10, 10); 
		MockSettingsManager settings = new MockSettingsManager();
		
		settings.setTweetSinceId(10);
		settings.setTweetMaxId(10);
		
		JSONArray jsonArray = requestTweets.requestTweets();
		Assert.assertEquals(jsonArray.length(), 5);
		
		long newestTweetId = 0;
		long oldestTweetId = Long.MAX_VALUE;
		for (int i = 0; i < jsonArray.length(); ++i)
		{
			try
			{
				JSONObject status = jsonArray.getJSONObject(i);
				JsonTweet tweet = new JsonTweet(status);

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
				Assert.fail("JSONException in When_requesting_latest_tweets_and_timeline_has_a_gap");
			}
		}
		
		TimelineAction gapCalculator = new TimelineGapCalculator(oldestTweetId, settings.getTweetMaxId()).calculate();
		Assert.assertTrue(gapCalculator.requestMoreTweets());
	}
}
