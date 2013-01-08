package com.eguy.twitterapi;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;

import android.util.Log;

import com.eguy.oauth.OAuthProviderAndConsumer;

public class TweetRequester implements IRequestTweets
{
	@Override
	public JSONArray requestTweets(String uri, OAuthProviderAndConsumer producerAndConsumer, RateCalculator rateCalculator, HttpClient client)
	{
		HttpGet get = new HttpGet(uri);

		try
		{
			producerAndConsumer.getConsumer().sign(get);
			rateCalculator.requestMade();
			
			String response = client.execute(get, new BasicResponseHandler());
			
			return new JSONArray(response);
		}
		catch (org.apache.http.client.HttpResponseException ex)
		{
			if (ex.getMessage().contains("Too Many Requests"))
			{
				Log.e("ScrollLock", "Exceeded twitter rate limit!", ex);
			}
		}
		catch (Exception e)
		{
			Log.e("ScrollLock", e.getClass().toString(), e);
		}
		return null;
	}
}
