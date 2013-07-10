package dev.emmaguy.twitterclient.twitterapi;

import org.json.JSONArray;

public interface IRequestTweets {
    JSONArray requestTweets();

    IRequestTweets updateRequestToFillGap(long sinceId, long maxId);

    boolean requestedLatestTweets();
}
