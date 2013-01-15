package com.eguy.twitterapi;

public class TimelineGapCalculator
{
	private long newestProcessedTweet;
	private long oldestTweetIdFromLastRequest;
	
	public TimelineGapCalculator(long oldestTweetId, long newestProcessedTweet)
	{
		this.oldestTweetIdFromLastRequest = oldestTweetId;
		this.newestProcessedTweet = newestProcessedTweet;	
	}

	public TimelineAction calculate()
	{	
		boolean shouldRequestMoreTweets = false;

		// the oldest tweet we just received is newer (has a higher id) than
		// the newest processed tweet
		if(oldestTweetIdFromLastRequest > newestProcessedTweet)
		{
			// so there could be a gap - request more tweets
			// as since_id is not inclusive, and there is no way to make it so,
			// we will end up doing 1 extra
			shouldRequestMoreTweets = true;
		}
		return new TimelineAction(oldestTweetIdFromLastRequest, newestProcessedTweet, shouldRequestMoreTweets);
	}
}