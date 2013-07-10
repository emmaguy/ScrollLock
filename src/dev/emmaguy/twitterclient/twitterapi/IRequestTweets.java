package dev.emmaguy.twitterclient.twitterapi;

import java.util.List;

import twitter4j.Status;

public interface IRequestTweets {
    List<Status> requestTweets();
    IRequestTweets updateRequestToFillGap(long sinceId, long maxId);
    boolean requestedLatestTweets();
}
