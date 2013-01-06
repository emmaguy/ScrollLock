package com.eguy.twitterapi;

public class TimelineGapCalculator
{
	private long oldestTweetId;
	private long newestTweetId;
	private long maxId;
	private boolean getLatestTweets;
	private long oldestProcessedTweet;
	private boolean shouldRequestMoreTweets = false;

	public TimelineGapCalculator(long oldestTweetId, long newestTweetId, long maxId, boolean getLatestTweets, long oldestProcessedTweet)
	{
		this.oldestTweetId = oldestTweetId;
		this.newestTweetId = newestTweetId;
		this.maxId = maxId;
		this.getLatestTweets = getLatestTweets;
		this.oldestProcessedTweet = oldestProcessedTweet;
	}

	public TimelineGapCalculator calculate()
	{
		long newestProcessedTweet = maxId;
		if (getLatestTweets)
		{
			newestProcessedTweet = oldestProcessedTweet;
		}
		
		// the oldest tweet we just received is newer (has a higher id) than
		// the newest processed tweet
		// so there could be a gap - request more tweets
		// as since_id is not inclusive, and there is no way to make it so,
		// we will end up doing 1 extra
		// request which will retrieve 0 tweets - to resolve this, could:
		// - implement a 'since id inclusive' - store the id of the tweet
		// before since id and compare against it
		// Note: can't reply on number of tweets given back by twitter -
		// 'count' is more a max count and doesn't guarantee that many
		if(oldestTweetId > newestProcessedTweet)
		{
			shouldRequestMoreTweets = true;
		}
		return this;
	}

	public boolean requestMoreTweest()
	{
		return shouldRequestMoreTweets;
	}

	public long getTopOfGap()
	{
		return newestTweetId;
	}

	public long getBottomOfGap()
	{
		return oldestTweetId - 1;
	}

}
