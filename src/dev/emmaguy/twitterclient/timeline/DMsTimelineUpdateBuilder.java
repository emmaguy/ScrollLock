package dev.emmaguy.twitterclient.timeline;

import android.content.ContentValues;
import android.util.Log;
import dev.emmaguy.twitterclient.db.TweetProvider;

public class DMsTimelineUpdateBuilder extends TimelineUpdateBuilder {

    @Override
    protected ContentValues createNewContentValues(twitter4j.Status tweet) {
	ContentValues v = super.createNewContentValues(tweet);
	Log.i("x", "adding  mention");
	v.put(TweetProvider.TWEET_IS_DM, true);
	return v;
    }
}
