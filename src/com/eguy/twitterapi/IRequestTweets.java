package com.eguy.twitterapi;

import org.json.JSONArray;

public interface IRequestTweets
{
	JSONArray requestTweets();
	IRequestTweets updateRequestToFillGap(long sinceId, long maxId);
}
