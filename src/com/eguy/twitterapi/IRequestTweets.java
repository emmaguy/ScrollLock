package com.eguy.twitterapi;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;

import com.eguy.oauth.OAuthProviderAndConsumer;

public interface IRequestTweets
{
	JSONArray requestTweets(String uri, OAuthProviderAndConsumer producerAndConsumer, RateCalculator rateCalculator, HttpClient client);
}
