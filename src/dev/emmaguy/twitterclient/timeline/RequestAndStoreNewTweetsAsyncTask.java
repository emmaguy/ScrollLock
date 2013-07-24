package dev.emmaguy.twitterclient.timeline;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.os.AsyncTask;
import android.util.Log;
import dev.emmaguy.twitterclient.ConsumerInfo;
import dev.emmaguy.twitterclient.db.IManageTweetStorage;

public class RequestAndStoreNewTweetsAsyncTask extends AsyncTask<Void, Void, Void> {
    private final IManageTweetStorage tweetStorer;
    private final IRequestTweets tweetRequester;

    private final String userToken;
    private final String userSecret;

    private long newestTweetIdFromLastRequest;
    private long sinceId;
    private long maxId;
    private int numberOfTweetsToRequest;
    private int pageId;
    private boolean isFillingGapInTimeline;
    private OnRefreshTimelineComplete onRefreshTimelineComplete;

    public RequestAndStoreNewTweetsAsyncTask(String userToken, String userSecret, IManageTweetStorage tweetStorer,
	    IRequestTweets tweetRequester, long newestTweetIdFromLastRequest, long sinceId, long maxId,
	    int numberOfTweetsToRequest, int pageId, boolean isFillingGapInTimeline,
	    OnRefreshTimelineComplete onRefreshTimelineComplete) {
	this.userToken = userToken;
	this.userSecret = userSecret;
	this.tweetStorer = tweetStorer;
	this.tweetRequester = tweetRequester;
	this.newestTweetIdFromLastRequest = newestTweetIdFromLastRequest;
	this.sinceId = sinceId;
	this.maxId = maxId;
	this.numberOfTweetsToRequest = numberOfTweetsToRequest;
	this.pageId = pageId;
	this.isFillingGapInTimeline = isFillingGapInTimeline;
	this.onRefreshTimelineComplete = onRefreshTimelineComplete;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
	try {
	    Log.i("xx", "newestTweetIdFromLastRequest: " + newestTweetIdFromLastRequest);
	    Log.i("xx", "sinceId: " + sinceId);
	    Log.i("xx", "maxId: " + maxId);
	    Log.i("xx", "numberOfTweetsToRequest: " + numberOfTweetsToRequest);
	    Log.i("xx", "pageId: " + pageId);
	    Log.i("xx", "isFillingGapInTimeline: " + isFillingGapInTimeline);

	    ConfigurationBuilder builder = new ConfigurationBuilder();
	    builder.setOAuthConsumerKey(ConsumerInfo.CONSUMER_KEY);
	    builder.setOAuthConsumerSecret(ConsumerInfo.CONSUMER_SECRET);
	    builder.setOAuthAccessToken(userToken);
	    builder.setOAuthAccessTokenSecret(userSecret);

	    TwitterFactory factory = new TwitterFactory(builder.build());
	    Twitter twitter = factory.getInstance();

	    tweetRequester.requestTweets(twitter, pageId, numberOfTweetsToRequest, sinceId, maxId);
	} catch (Exception e) {
	    Log.e("ScrollLock", e.getClass().toString(), e);
	}
	return null;
    }

    @Override
    protected void onPostExecute(Void v) {
	TimelineUpdate update = tweetRequester.getTimelineUpdate();
	if (!update.hasTweets()) {
	    refreshComplete();
	    return;
	}

	tweetStorer.addTweets(tweetRequester.getTweets());

	Log.i("xx", "getNewestTweetId: " + update.getNewestTweetId() + " getTweetSinceId:" + tweetRequester.getTweetSinceId());
	if (update.getNewestTweetId() > tweetRequester.getTweetSinceId()) {
	    Log.i("xx", "setting sinceid to: " + update.getNewestTweetId());
	    tweetRequester.setTweetSinceId(update.getNewestTweetId());
	}

	if (!isFillingGapInTimeline && update.getOldestTweetId() > newestTweetIdFromLastRequest) {
	    new RequestAndStoreNewTweetsAsyncTask(userToken, userSecret, tweetStorer, tweetRequester,
		    update.getNewestTweetId(), newestTweetIdFromLastRequest, update.getOldestTweetId() - 1,
		    numberOfTweetsToRequest, pageId, true, onRefreshTimelineComplete).execute();
	    return;
	} else if (isFillingGapInTimeline && sinceId > 0) {
	    new RequestAndStoreNewTweetsAsyncTask(userToken, userSecret, tweetStorer, tweetRequester,
		    newestTweetIdFromLastRequest, sinceId, maxId, numberOfTweetsToRequest, ++pageId, true,
		    onRefreshTimelineComplete).execute();
	    return;
	}

	refreshComplete();
    }

    public void refreshComplete() {
	Log.i("xx", "no tweets, setting maxid to: " + newestTweetIdFromLastRequest);
	tweetRequester.setTweetMaxId(newestTweetIdFromLastRequest);
	onRefreshTimelineComplete.refreshComplete();
    }
}