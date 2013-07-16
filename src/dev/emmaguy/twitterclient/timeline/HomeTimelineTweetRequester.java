package dev.emmaguy.twitterclient.timeline;

import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.ContentValues;
import dev.emmaguy.twitterclient.db.TweetProvider;

public class HomeTimelineTweetRequester implements IRequestTweets {

    private List<Status> statuses;

    @Override
    public void requestTweets(Twitter twitter, int pageId, int numberOfTweetsToRequest, long sinceId, long maxId)
	    throws TwitterException {
	Paging p;

	if (sinceId <= 0) {
	    p = new Paging(pageId, numberOfTweetsToRequest);
	} else if (maxId <= 0) {
	    p = new Paging(pageId, numberOfTweetsToRequest, sinceId);
	} else {
	    p = new Paging(pageId, numberOfTweetsToRequest, sinceId, maxId);
	}

	statuses = twitter.getHomeTimeline(p);
    }

    @Override
    public ContentValues[] getTweets() {
	final int size = statuses.size();
	ContentValues[] values = new ContentValues[size];
	for (int i = 0; i < size; i++) {
	    values[i] = createNewContentValues(statuses.get(i));
	}
	return values;
    }

    @Override
    public TimelineUpdate getTimelineUpdate() {
	return new TimelineUpdate().buildFromTweets(statuses);
    }

    private ContentValues createNewContentValues(twitter4j.Status tweet) {
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
	    tweetValue.put(TweetProvider.TWEET_RETWEETED_BY_USERNAME, tweet.getRetweetedStatus().getUser().getScreenName());
	    tweetValue.put(TweetProvider.TWEET_RETWEET_PROFILE_PIC_URL, tweet.getRetweetedStatus().getUser()
		    .getOriginalProfileImageURL());
	}
	return tweetValue;
    }
}
