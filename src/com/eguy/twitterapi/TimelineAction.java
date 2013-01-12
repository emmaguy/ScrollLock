package com.eguy.twitterapi;

public class TimelineAction
{
	private long oldestTweetId;
	private long newestProcessedTweet;
	private boolean shouldRequestMoreTweets;

	public TimelineAction(long oldestTweetId, long newestProcessedTweet, boolean shouldRequestMoreTweets)
	{
		this.oldestTweetId = oldestTweetId;
		this.newestProcessedTweet = newestProcessedTweet;
		this.shouldRequestMoreTweets = shouldRequestMoreTweets;
	}
	
	public boolean requestMoreTweets()
	{
		return shouldRequestMoreTweets;
	}

	public long getTopOfGap()
	{
		return newestProcessedTweet;
	}

	public long getBottomOfGap()
	{
		return oldestTweetId - 1;
	}
}