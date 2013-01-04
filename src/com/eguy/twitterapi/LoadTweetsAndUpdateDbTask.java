package com.eguy.twitterapi;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.eguy.SettingsManager;
import com.eguy.db.TweetProvider;
import com.eguy.oauth.OAuthProviderAndConsumer;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class LoadTweetsAndUpdateDbTask extends AsyncTask<Void, Void, JSONArray>
{
    private final String HOME_TIMELINE_URL = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    private static final int MAX_NUMBER_OF_REQUESTS_PER_WINDOW = 5;
    private static final int WINDOW_LENGTH = 15;

    private OAuthProviderAndConsumer producerAndConsumer;
    private SettingsManager settingsManager;
    private static RateCalculator rateCalculator;

    private HttpClient client;
    private Context context;

    private long sinceId;
    private long maxId;
    private int numberOfTweetsToRequest;

    static
    {
        rateCalculator = new RateCalculator(MAX_NUMBER_OF_REQUESTS_PER_WINDOW, WINDOW_LENGTH);
    }

    public LoadTweetsAndUpdateDbTask(OAuthProviderAndConsumer producerAndConsumer, SettingsManager settingsManager, Context context, long sinceId, long maxId, int numberOfTweetsToRequest)
    {
        this.producerAndConsumer = producerAndConsumer;
        this.settingsManager = settingsManager;
        this.context = context;
        this.sinceId = sinceId;
        this.maxId = maxId;
        this.numberOfTweetsToRequest = numberOfTweetsToRequest;

        client = new HttpClientBuilder().Build();
    }

    @Override
    protected JSONArray doInBackground(Void... arg0)
    {
        try
        {
            if (!rateCalculator.canMakeRequest())
            {
                Log.d("ScrollLock", "Not performing request to twitter as it may exceed the rate limit");
                settingsManager.setTweetMaxId(maxId);
                return null;
            }

            Uri sUri = Uri.parse(HOME_TIMELINE_URL);
            Uri.Builder builder = sUri.buildUpon();
            builder.appendQueryParameter("screen_name", settingsManager.getUsername());
            builder.appendQueryParameter("count", String.valueOf(numberOfTweetsToRequest));

            if (sinceId != 0)
            {
                builder.appendQueryParameter("since_id", String.valueOf(sinceId));
                Log.d("ScrollLock", "Requesting since_id: " + sinceId);
            }
            if (maxId != 0)
            {
                builder.appendQueryParameter("max_id", String.valueOf(maxId));
                Log.d("ScrollLock", "Requesting max_id: " + maxId);
            }

            String uri = builder.build().toString();
            HttpGet get = new HttpGet(uri);
            producerAndConsumer.getConsumer().sign(get);

            String response = client.execute(get, new BasicResponseHandler());
            rateCalculator.requestMade();

            return new JSONArray(response);
        } catch (org.apache.http.client.HttpResponseException ex)
        {
            if (ex.getMessage().contains("Too Many Requests"))
            {
                Log.e("ScrollLock", "Exceeded twitter rate limit!", ex);
            }
        } catch (Exception e)
        {
            Log.e("ScrollLock", e.getClass().toString(), e);
        }

        return null;
    }

    protected void onPostExecute(JSONArray jsonArray)
    {
        if (jsonArray == null)
            return;

        try
        {
            long newestTweetId = 0;
            long oldestTweetId = Long.MAX_VALUE;
            ContentValues[] tweets = new ContentValues[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); ++i)
            {
                JSONObject status = jsonArray.getJSONObject(i);
                JsonTweet tweet = new JsonTweet(status);

                ContentValues tweetValue = new ContentValues();
                tweetValue.put(TweetProvider.TWEET_ID, tweet.getTweetId());
                tweetValue.put(TweetProvider.TWEET_TEXT, tweet.getText());
                tweetValue.put(TweetProvider.TWEET_CREATED_AT, tweet.getTweetCreatedAt());
                tweetValue.put(TweetProvider.TWEET_USER_ID, tweet.getUserId());
                tweetValue.put(TweetProvider.TWEET_USERNAME, tweet.getUsername());
                tweetValue.put(TweetProvider.TWEET_PROFILE_PIC_URL, tweet.getProfilePicUrl());
                tweets[i] = tweetValue;

                if (tweet.getTweetId() > newestTweetId)
                {
                    newestTweetId = tweet.getTweetId();
                }
                if (tweet.getTweetId() < oldestTweetId)
                {
                    oldestTweetId = tweet.getTweetId();
                }
            }
            Log.d("ScrollLock", "Parsed: " + tweets.length + " tweets");

            if(tweets.length == 0)
            {
                Log.d("ScrollLock", "No tweets found");
                if(maxId != 0)
                {
                    Log.d("ScrollLock", "Setting max_id to: " + maxId);
                    settingsManager.setTweetMaxId(maxId);
                }

                return;
            }

            // if we have processed a newer tweet than what we already have, store its id
            if(newestTweetId > settingsManager.getTweetSinceId() && newestTweetId != 0)
            {
                Log.d("ScrollLock", "Setting since_id to: " + newestTweetId);
                settingsManager.setTweetSinceId(newestTweetId);
            }

            // the oldest tweet we just received is newer (has a higher id) than the newest processed tweet
            // so there could be a gap - request more tweets
            // as since_id is not inclusive, and there is no way to make it so, we will end up doing 1 extra
            // request which will retrieve 0 tweets - to resolve this, could:
            // - implement a 'since id inclusive' - store the id of the tweet before since id and compare against it
            // - alternatively could say "if I ask twitter for 10 tweets and it gives me back 8, I know I'm done"
            if(oldestTweetId > settingsManager.getTweetMaxId())
            {
                new LoadTweetsAndUpdateDbTask(producerAndConsumer, settingsManager, context,
                        settingsManager.getTweetMaxId(), oldestTweetId - 1, 200).execute();
            }

            context.getContentResolver().bulkInsert(TweetProvider.TWEET_URI, tweets);
        } catch (JSONException e)
        {
            Log.e("ScrollLock", e.getClass().toString(), e);
        }
    }
}