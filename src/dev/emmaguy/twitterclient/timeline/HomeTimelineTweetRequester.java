package dev.emmaguy.twitterclient.timeline;

import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class HomeTimelineTweetRequester implements IRequestTweets {

    @Override
    public List<Status> requestTweets(Twitter twitter, int pageId, int numberOfTweetsToRequest, long sinceId, long maxId) throws TwitterException {
	Paging p;

	if (sinceId <= 0) {
	    p = new Paging(pageId, numberOfTweetsToRequest);
	} else if (maxId <= 0) {
	    p = new Paging(pageId, numberOfTweetsToRequest, sinceId);
	} else {
	    p = new Paging(pageId, numberOfTweetsToRequest, sinceId, maxId);
	}

	List<twitter4j.Status> statuses = twitter.getHomeTimeline(p);

	return statuses;
    }

}
