package tests.helpers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import dev.emmaguy.twitterclient.twitterapi.IRequestTweets;


public class MockTweetRequester implements IRequestTweets
{
	private int numberOfTweetsToRequest;
	private long sinceId;
	private long maxId;

	public MockTweetRequester(int numberOfTweetsToRequest, long sinceId, long maxId)
	{
		this.numberOfTweetsToRequest = numberOfTweetsToRequest;
		this.sinceId = sinceId;
		this.maxId = maxId;		
	}
	
	@Override
	public JSONArray requestTweets()
	{
		Log.d("ScrollLock", "Requesting tweets: sinceId: " + sinceId + ", maxid: " + maxId);
		String json = "";
		for(int i = 0; i < numberOfTweetsToRequest; i++)
		{
			if(requestedLatestTweets())
			{
				// if we get latest assume we get later than the id we give
				// TODO: also simulate getting latest where some tweets retrieved are older
				json += getTweet(sinceId++);
				if(i + 1 < numberOfTweetsToRequest)
					json += ",";
			}
			else if(sinceId < maxId)
			{
				// if we are filling a gap, retrieve these values
				json += getTweet(maxId--);
				if(i + 1 < numberOfTweetsToRequest)
					json += ",";
			}
		}
		
		try
		{
			return new JSONArray("[" + json + "]");
		}
		catch (JSONException e)
		{
			
		}
		
		return new JSONArray();
	}
	
	private JSONObject getTweet(long tweetId)
	{
		try
		{
			return new JSONObject(String.format("  {    \"text\": \"blah\",   " +
					" \"id\": %d,    " +
					" \"created_at\": 1,    " +
					" \"user\": {      " +
						"\"id\": 1,      " +
						"\"profile_image_url\": 1,      " +
						"\"screen_name\": \"username\"   } } ", tweetId));
		}
		catch (JSONException e)
		{
			
		}
		return null;
	}

	@Override
	public IRequestTweets updateRequestToFillGap(long sinceId, long maxId)
	{
		this.sinceId = sinceId;
		this.maxId = maxId;
		Log.d("ScrollLock", "Updating sinceId: " + sinceId + " and maxId: " + maxId);
		return this;
	}

	@Override
	public boolean requestedLatestTweets()
	{
		return maxId == 0;
	}
}
