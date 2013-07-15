package dev.emmaguy.twitterclient.timeline;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.os.AsyncTask;
import android.util.Log;
import dev.emmaguy.twitterclient.ConsumerInfo;
import dev.emmaguy.twitterclient.IContainSettings;
import dev.emmaguy.twitterclient.db.IManageTweetStorage;

public class RequestAndStoreNewTweetsAsyncTask extends AsyncTask<Void, Void, Void> {
    private final IContainSettings settingsManager;
    private final IManageTweetStorage tweetStorer;
    private final IRequestTweets tweetRequester;

    private long newestTweetIdFromLastRequest;
    private long sinceId;
    private long maxId;
    private int numberOfTweetsToRequest;
    private int pageId;
    private boolean isFillingGapInTimeline;

    public RequestAndStoreNewTweetsAsyncTask(IContainSettings settingsManager, IManageTweetStorage tweetStorer,
	    IRequestTweets tweetRequester, long newestTweetIdFromLastRequest, long sinceId, long maxId,
	    int numberOfTweetsToRequest, int pageId, boolean isFillingGapInTimeline) {
	this.settingsManager = settingsManager;
	this.tweetStorer = tweetStorer;
	this.tweetRequester = tweetRequester;
	this.newestTweetIdFromLastRequest = newestTweetIdFromLastRequest;
	this.sinceId = sinceId;
	this.maxId = maxId;
	this.numberOfTweetsToRequest = numberOfTweetsToRequest;
	this.pageId = pageId;
	this.isFillingGapInTimeline = isFillingGapInTimeline;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
	try {
	    ConfigurationBuilder builder = new ConfigurationBuilder();
	    builder.setOAuthConsumerKey(ConsumerInfo.CONSUMER_KEY);
	    builder.setOAuthConsumerSecret(ConsumerInfo.CONSUMER_SECRET);
	    builder.setOAuthAccessToken(settingsManager.getUserToken());
	    builder.setOAuthAccessTokenSecret(settingsManager.getUserTokenSecret());

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
	    settingsManager.setTweetMaxId(newestTweetIdFromLastRequest);
	    return;
	}

	tweetStorer.addTweets(tweetRequester.getTweets());

	if (update.getNewestTweetId() > settingsManager.getTweetSinceId()) {
	    settingsManager.setTweetSinceId(update.getNewestTweetId());
	}

	if (!isFillingGapInTimeline && update.getOldestTweetId() > newestTweetIdFromLastRequest) {
	    new RequestAndStoreNewTweetsAsyncTask(settingsManager, tweetStorer, tweetRequester,
		    update.getNewestTweetId(), newestTweetIdFromLastRequest, update.getOldestTweetId() - 1,
		    numberOfTweetsToRequest, pageId, true).execute();
	} else if (isFillingGapInTimeline) {
	    new RequestAndStoreNewTweetsAsyncTask(settingsManager, tweetStorer, tweetRequester,
		    newestTweetIdFromLastRequest, sinceId, maxId, numberOfTweetsToRequest, ++pageId, true).execute();
	}
    }
}