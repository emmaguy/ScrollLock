package com.eguy.twitterapi;

public class TimelineGapCalculator
{
	private long oldestProcessedTweet;
	private long oldestTweetId;
	
	public TimelineGapCalculator(long oldestTweetId, long oldestProcessedTweet)
	{
		this.oldestTweetId = oldestTweetId;
		this.oldestProcessedTweet = oldestProcessedTweet;	
	}

	public TimelineAction calculate()
	{	
		boolean shouldRequestMoreTweets = false;

		// the oldest tweet we just received is newer (has a higher id) than
		// the newest processed tweet
		if(oldestTweetId > oldestProcessedTweet)
		{
			// so there could be a gap - request more tweets
			// as since_id is not inclusive, and there is no way to make it so,
			// we will end up doing 1 extra
			shouldRequestMoreTweets = true;
		}
		return new TimelineAction(oldestTweetId, oldestProcessedTweet, shouldRequestMoreTweets);
	}
}