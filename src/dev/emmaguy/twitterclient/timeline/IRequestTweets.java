package dev.emmaguy.twitterclient.timeline;

import android.content.ContentValues;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public interface IRequestTweets {
    void requestTweets(Twitter twitter, int pageId, int numberOfTweetsToRequest, long sinceId, long maxId) throws TwitterException;
    
    ContentValues[] getTweets();
    TimelineUpdate getTimelineUpdate();
    
    long getTweetMaxId();
    long getTweetSinceId();
    int getNumberOfTweetsToRequest();

    void setTweetMaxId(long maxId);
    void setTweetSinceId(long sinceId);
}
