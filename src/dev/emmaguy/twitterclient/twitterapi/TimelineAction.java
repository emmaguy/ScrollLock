package dev.emmaguy.twitterclient.twitterapi;

public class TimelineAction {
    private long oldestTweetIdFromLastRequest;
    private long newestProcessedTweet;
    private boolean shouldRequestMoreTweets;

    public TimelineAction(long oldestTweetIdFromLastRequest, long newestProcessedTweet, boolean shouldRequestMoreTweets) {
	this.oldestTweetIdFromLastRequest = oldestTweetIdFromLastRequest;
	this.newestProcessedTweet = newestProcessedTweet;
	this.shouldRequestMoreTweets = shouldRequestMoreTweets;
    }

    public boolean requestMoreTweets() {
	return shouldRequestMoreTweets;
    }

    public long getTopOfGap() {
	return newestProcessedTweet;
    }

    public long getBottomOfGap() {
	return oldestTweetIdFromLastRequest - 1;
    }
}