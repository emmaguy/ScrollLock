package dev.emmaguy.twitterclient.timeline;

import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.ContentValues;
import dev.emmaguy.twitterclient.IContainSettings;
import dev.emmaguy.twitterclient.db.TweetProvider;

public class MentionsTimelineTweetRequester implements IRequestTweets {

    private List<Status> statuses;
    private IContainSettings settings;

    public MentionsTimelineTweetRequester(IContainSettings settings){
	this.settings = settings;
    }
    
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
	
	statuses = twitter.getMentionsTimeline(p);
    }

    @Override
    public ContentValues[] getTweets() {
	final int size = statuses.size();
	ContentValues[] values = new ContentValues[size];
	for (int i = 0; i < size; i++) {
	    Status tweet = statuses.get(i);
	    ContentValues tweetValue = new ContentValues();
	    tweetValue.put(TweetProvider.TWEET_ID, tweet.getId());
	    tweetValue.put(TweetProvider.TWEET_TEXT, tweet.getText());
	    tweetValue.put(TweetProvider.TWEET_CREATED_AT, tweet.getCreatedAt().toString());
	    tweetValue.put(TweetProvider.TWEET_USER_ID, tweet.getUser().getId());
	    tweetValue.put(TweetProvider.TWEET_USERNAME, tweet.getUser().getScreenName());
	    tweetValue.put(TweetProvider.TWEET_PROFILE_PIC_URL, tweet.getUser().getOriginalProfileImageURL());
	    tweetValue.put(TweetProvider.TWEET_RETWEET_COUNT, tweet.getRetweetCount());
	    tweetValue.put(TweetProvider.TWEET_IS_MENTION, true);
	    values[i] = tweetValue;
	}
	return values;
    }

    @Override
    public TimelineUpdate getTimelineUpdate() {
	return new TimelineUpdate().buildFromTweets(statuses);
    }

    @Override
    public long getTweetMaxId() {
	return settings.getMentionsTweetMaxId();
    }

    @Override
    public long getTweetSinceId() {
	return settings.getMentionsTweetSinceId();
    }

    @Override
    public int getNumberOfTweetsToRequest() {
	return settings.getNumberOfTweetsToRequest();
    }

    @Override
    public void setTweetMaxId(long maxId) {
	settings.setMentionsTweetMaxId(maxId);
    }

    @Override
    public void setTweetSinceId(long sinceId) {
	settings.setMentionsTweetSinceId(sinceId);
    }
}
