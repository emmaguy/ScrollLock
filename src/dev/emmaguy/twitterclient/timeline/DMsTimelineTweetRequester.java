package dev.emmaguy.twitterclient.timeline;

import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class DMsTimelineTweetRequester implements IRequestTweets {

    @Override
    public List<Status> requestTweets(Twitter twitter, int pageId, int numberOfTweetsToRequest, long sinceId, long maxId)
	    throws TwitterException {
	return null;
    }

}
