package com.eguy.twitterapi;

import oauth.signpost.OAuthConsumer;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;

import com.eguy.oauth.OAuthProviderAndConsumer;

public interface IRequestTweets
{
	JSONArray requestTweets(String uri);
}
