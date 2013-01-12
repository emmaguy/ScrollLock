package com.eguy.twitterapi;

import oauth.signpost.OAuthConsumer;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;

import android.util.Log;

import com.eguy.oauth.OAuthProviderAndConsumer;

public class TweetRequester implements IRequestTweets
{
	private OAuthConsumer oAuthConsumer;

	public TweetRequester(OAuthConsumer consumer)
	{
		this.oAuthConsumer = consumer;
	}
	
	@Override
	public JSONArray requestTweets(String uri)
	{
		HttpGet get = new HttpGet(uri);

		try
		{
			oAuthConsumer.sign(get);
			
			HttpClient client = new HttpClientBuilder().build();			
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
