package dev.emmaguy.twitterclient.timeline;

import java.util.List;

import twitter4j.DirectMessage;

import android.content.ContentValues;
import dev.emmaguy.twitterclient.db.TweetProvider;

public class TimelineUpdateBuilder implements IBuildTimelineUpdates {
    private long newestTweetId = 0;
    private long oldestTweetId = Long.MAX_VALUE;

    public TimelineUpdate build(List<twitter4j.Status> statuses) {
	final ContentValues[] tweets = new ContentValues[statuses.size()];

	for (int i = 0, size = statuses.size(); i < size; i++) {
	    twitter4j.Status s = statuses.get(i);
	    tweets[i] = createNewContentValues(s);

	    final long tweetId = s.getId();
	    if (tweetId > newestTweetId) {
		newestTweetId = tweetId;
	    }
	    if (tweetId < oldestTweetId) {
		oldestTweetId = tweetId;
	    }
	}

	return new TimelineUpdate(tweets, newestTweetId, oldestTweetId);
    }

    protected ContentValues createNewContentValues(twitter4j.Status tweet) {
	ContentValues tweetValue = new ContentValues();
	tweetValue.put(TweetProvider.TWEET_ID, tweet.getId());
	tweetValue.put(TweetProvider.TWEET_TEXT, tweet.getText());
	tweetValue.put(TweetProvider.TWEET_CREATED_AT, tweet.getCreatedAt().toString());
	tweetValue.put(TweetProvider.TWEET_USER_ID, tweet.getUser().getId());
	tweetValue.put(TweetProvider.TWEET_USERNAME, tweet.getUser().getScreenName());
	tweetValue.put(TweetProvider.TWEET_PROFILE_PIC_URL, tweet.getUser().getOriginalProfileImageURL());
	tweetValue.put(TweetProvider.TWEET_RETWEET_COUNT, tweet.getRetweetCount());

	if (tweet.isRetweet()) {
	    tweetValue.put(TweetProvider.TWEET_TEXT, tweet.getRetweetedStatus().getText());
	    tweetValue.put(TweetProvider.TWEET_RETWEETED_BY_USER_ID, tweet.getRetweetedStatus().getUser().getId());
	    tweetValue.put(TweetProvider.TWEET_RETWEETED_BY_USERNAME, tweet.getRetweetedStatus().getUser().getName());
	    tweetValue.put(TweetProvider.TWEET_RETWEET_PROFILE_PIC_URL, tweet.getRetweetedStatus().getUser()
		    .getOriginalProfileImageURL());
	}
	return tweetValue;
    }
}