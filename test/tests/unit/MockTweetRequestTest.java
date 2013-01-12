package tests.unit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import tests.helpers.MockTweetRequester;

import com.eguy.twitterapi.JsonTweet;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MockTweetRequestTest
{
	@Test
	public void testWhen_building_mock_tweet_id_20()
	{
		MockTweetRequester requestTweets = new MockTweetRequester(1, 20, 25);
		JSONArray jsonArray = requestTweets.requestTweets();

		Assert.assertEquals(jsonArray.length(), 1);

		try
		{
			JSONObject status = jsonArray.getJSONObject(0);
			JsonTweet tweet = new JsonTweet(status);

			Assert.assertEquals(tweet.getTweetId(), 20);
		}
		catch (JSONException e)
		{
			Assert.fail("Json exception at testWhen_building_mock_tweet_id_20");
		}
	}
	
	
	@Test
	public void testWhen_building_10_mock_tweets_over_max_boundary_should_get_6()
	{
		MockTweetRequester requestTweets = new MockTweetRequester(10, 10, 15);
		JSONArray jsonArray = requestTweets.requestTweets();

		Assert.assertEquals(jsonArray.length(), 6);
	}
	
	@Test
	public void testWhen_building_5_mock_tweets_should_get_5_tweets_back()
	{
		MockTweetRequester requestTweets = new MockTweetRequester(5, 10, 25);
		JSONArray jsonArray = requestTweets.requestTweets();

		Assert.assertEquals(jsonArray.length(), 5);
	}
}
