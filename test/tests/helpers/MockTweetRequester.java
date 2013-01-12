package tests.helpers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eguy.twitterapi.IRequestTweets;


public class MockTweetRequester implements IRequestTweets
{
	private int numberOfTweetsToRequest;
	private int sinceId;
	private int maxId;

	public MockTweetRequester(int numberOfTweetsToRequest, int sinceId, int maxId)
	{
		this.numberOfTweetsToRequest = numberOfTweetsToRequest;
		this.sinceId = sinceId;
		this.maxId = maxId;		
	}
	
	@Override
	public JSONArray requestTweets()
	{
		String json = "";
		int tweetId = sinceId;
		for(int i = 0; i < numberOfTweetsToRequest; i++)
		{
			if(tweetId <= maxId)
			{
				json += getTweet(tweetId++);
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
	
	private JSONObject getTweet(int tweetId)
	{
		try
		{
			return new JSONObject(String.format("  {    \"text\": \"blah\",   " +
					" \"id\": %d,    " +
					" \"user\": {      " +
						"\"id\": 1,      " +
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
		return null;
	}
}
