package dev.emmaguy.twitterclient.twitterapi;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import dev.emmaguy.twitterclient.db.TweetProvider;

public class FakeTweetInserterTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private static int counter = 0;

    public FakeTweetInserterTask(Context context) {
	this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
	ContentValues[] values = new ContentValues[20];
	for (int i = 0; i < 20; i++) {
	    ContentValues tweetValue = new ContentValues();
	    tweetValue.put(TweetProvider.TWEET_ID, Long.MAX_VALUE - counter);
	    tweetValue.put(TweetProvider.TWEET_TEXT, "Generated tweet: " + counter++);
	    tweetValue.put(TweetProvider.TWEET_CREATED_AT, "Sun Jun 31 23:35:11 +0000 2012");
	    tweetValue.put(TweetProvider.TWEET_USER_ID, "7762342");
	    tweetValue.put(TweetProvider.TWEET_USERNAME, "Nieuwsblad_be");
	    tweetValue.put(TweetProvider.TWEET_PROFILE_PIC_URL,
		    "http://a0.twimg.com/profile_images/26110972/avatarnbo_normal.jpg");

	    if (i % 3 == 0) {
		tweetValue.put(TweetProvider.TWEET_RETWEET_COUNT, 10);
		tweetValue.put(TweetProvider.TWEET_RETWEET_PROFILE_PIC_URL,
			"https://twimg0-a.akamaihd.net/profile_images/2864880241/5cdba007bfc99a8334a11bcbb1b8c8ac.png");
		tweetValue.put(TweetProvider.TWEET_RETWEETED_BY_USER_ID, "7762343");
		tweetValue.put(TweetProvider.TWEET_RETWEETED_BY_USERNAME, "Nieuwsblad_be");
	    } else {
		tweetValue.put(TweetProvider.TWEET_RETWEET_COUNT, 0);
	    }
	    values[i] = tweetValue;
	}
	context.getContentResolver().bulkInsert(TweetProvider.TWEET_URI, values);

	return null;
    }
}
