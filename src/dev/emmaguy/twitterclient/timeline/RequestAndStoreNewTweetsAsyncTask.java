package dev.emmaguy.twitterclient.timeline;

import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;
import dev.emmaguy.twitterclient.ConsumerInfo;
import dev.emmaguy.twitterclient.IContainSettings;
import dev.emmaguy.twitterclient.db.IStoreTweets;

public class RequestAndStoreNewTweetsAsyncTask extends AsyncTask<Void, Void, List<Status>> {
    private final IContainSettings settingsManager;
    private final IStoreTweets tweetStorer;
    private long newestTweetIdFromLastRequest;
    private long sinceId;
    private long maxId;
    private int numberOfTweetsToRequest;
    private int pageId;

    public RequestAndStoreNewTweetsAsyncTask(IContainSettings settingsManager, IStoreTweets tweetStorer,
	    long newestTweetIdFromLastRequest, long sinceId, long maxId, int numberOfTweetsToRequest, int pageId) {
	this.settingsManager = settingsManager;
	this.tweetStorer = tweetStorer;
	this.newestTweetIdFromLastRequest = newestTweetIdFromLastRequest;
	this.sinceId = sinceId;
	this.maxId = maxId;
	this.numberOfTweetsToRequest = numberOfTweetsToRequest;
	this.pageId = pageId;
    }

    @Override
    protected List<twitter4j.Status> doInBackground(Void... arg0) {
	try {
	    ConfigurationBuilder builder = new ConfigurationBuilder();
	    builder.setOAuthConsumerKey(ConsumerInfo.CONSUMER_KEY);
	    builder.setOAuthConsumerSecret(ConsumerInfo.CONSUMER_SECRET);
	    builder.setOAuthAccessToken(settingsManager.getUserToken());
	    builder.setOAuthAccessTokenSecret(settingsManager.getUserTokenSecret());

	    TwitterFactory factory = new TwitterFactory(builder.build());
	    Twitter twitter = factory.getInstance();

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
	} catch (Exception e) {
	    Log.e("ScrollLock", e.getClass().toString(), e);
	}
	return null;
    }

    protected void onPostExecute(List<twitter4j.Status> statuses) {
	if (statuses == null || statuses.size() <= 0) {
	    settingsManager.setTweetMaxId(newestTweetIdFromLastRequest);
	    return;
	}

	long newestTweetId = 0;
	long oldestTweetId = Long.MAX_VALUE;

	ContentValues[] tweets = new ContentValues[statuses.size()];
	for (int i = 0; i < statuses.size(); ++i) {
	    twitter4j.Status s = statuses.get(i);
	    tweets[i] = new TweetBuilder(s).build();

	    final long tweetId = s.getId();

	    if (tweetId > newestTweetId) {
		newestTweetId = tweetId;
	    }
	    if (tweetId < oldestTweetId) {
		oldestTweetId = tweetId;
	    }
	}

	tweetStorer.addTweets(tweets);

	if (newestTweetId > settingsManager.getTweetSinceId()) {
	    settingsManager.setTweetSinceId(newestTweetId);
	}

	if (newestTweetId - 1 <= maxId || oldestTweetId > newestTweetIdFromLastRequest) {
	    sinceId = newestTweetIdFromLastRequest;
	    if (maxId > 0) {
		pageId++;
		newestTweetIdFromLastRequest = newestTweetId;
	    } else {
		// get tweets in the gap up until this value
		maxId = oldestTweetId - 1;
	    }
	    
	    new RequestAndStoreNewTweetsAsyncTask(settingsManager, tweetStorer, newestTweetIdFromLastRequest, sinceId,
		    maxId, numberOfTweetsToRequest, pageId).execute();
	}
    }
}