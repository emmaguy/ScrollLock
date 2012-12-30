package com.eguy.twitterapi;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import com.eguy.db.TweetProvider;

import java.util.Random;

public class FakeTweetInserterTask extends AsyncTask<Void, Void, Void>
{
    private Context context;
    private static int counter = 0;

    public FakeTweetInserterTask(Context context)
    {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        ContentValues[] values = new ContentValues[20];
        for (int i = 0; i < 20; i++)
        {
            ContentValues tweetValue = new ContentValues();
            tweetValue.put(TweetProvider.TWEET_ID, new Random().nextLong());
            tweetValue.put(TweetProvider.TWEET_TEXT, "Generated tweet: " + counter++);
            tweetValue.put(TweetProvider.TWEET_CREATED_AT, "Sun Dec 30 05:35:11 +0000 2012");
            tweetValue.put(TweetProvider.TWEET_USER_ID, "7762342");
            tweetValue.put(TweetProvider.TWEET_USERNAME, "Nieuwsblad_be");
            tweetValue.put(TweetProvider.TWEET_PROFILE_PIC_URL, "http://a0.twimg.com/profile_images/26110972/avatarnbo_normal.jpg");
            values[i] = tweetValue;
        }
        context.getContentResolver().bulkInsert(TweetProvider.TWEET_URI, values);

        return null;
    }
}
