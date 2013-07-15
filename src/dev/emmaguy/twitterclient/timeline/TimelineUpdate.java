package dev.emmaguy.twitterclient.timeline;

import java.util.List;

public class TimelineUpdate {
    private long newestTweetId = 0;
    private long oldestTweetId = Long.MAX_VALUE;
    private boolean hasTweets = false;

    public TimelineUpdate buildFromTweets(List<twitter4j.Status> statuses) {
	
	if(statuses == null) return this;
	
	for (twitter4j.Status status : statuses) {
	    final long tweetId = status.getId();
	    if (tweetId > newestTweetId) {
		newestTweetId = tweetId;
	    }
	    if (tweetId < oldestTweetId) {
		oldestTweetId = tweetId;
	    }
	    hasTweets = true;
	}
	return this;
    }
    
    public TimelineUpdate buildFromDMs(List<twitter4j.DirectMessage> directMessages) {

	if(directMessages == null) return this;
	
	for (twitter4j.DirectMessage dm : directMessages) {
	    final long tweetId = dm.getId();
	    if (tweetId > newestTweetId) {
		newestTweetId = tweetId;
	    }
	    if (tweetId < oldestTweetId) {
		oldestTweetId = tweetId;
	    }
	    hasTweets = true;
	}
	return this;
    }

    public boolean hasTweets() {
	return hasTweets;
    }

    public long getNewestTweetId() {
	return newestTweetId;
    }

    public long getOldestTweetId() {
	return oldestTweetId;
    }
}
