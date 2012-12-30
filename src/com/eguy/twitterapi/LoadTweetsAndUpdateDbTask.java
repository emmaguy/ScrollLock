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

import java.util.Random;

public class LoadTweetsAndUpdateDbTask extends AsyncTask<Void, Void, JSONArray>
{
    private final String HOME_TIMELINE_URL = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    private OAuthProviderAndConsumer producerAndConsumer;
    private SettingsManager settingsManager;
    private Context context;
    private HttpClient client;

    public LoadTweetsAndUpdateDbTask(OAuthProviderAndConsumer producerAndConsumer, SettingsManager settingsManager, Context context)
    {
        this.producerAndConsumer = producerAndConsumer;
        this.settingsManager = settingsManager;
        this.context = context;

        client = new HttpClientBuilder().Builder();
    }

    @Override
    protected JSONArray doInBackground(Void... arg0)
    {
        try
        {
            Uri sUri = Uri.parse(HOME_TIMELINE_URL);
            Uri.Builder builder = sUri.buildUpon();
            builder.appendQueryParameter("screen_name", settingsManager.getUsername());
            builder.appendQueryParameter("count", "20");

//            if(settingsManager.getTweetSinceId() != 0)
//            {
//                builder.appendQueryParameter("since_id", String.valueOf(settingsManager.getTweetSinceId()));
//            }
//            if(settingsManager.getTweetMaxId() != 0)
//            {
//                builder.appendQueryParameter("max_id", String.valueOf(settingsManager.getTweetMaxId()));
//            }

            String uri = builder.build().toString();
            HttpGet get = new HttpGet(uri);
            producerAndConsumer.getConsumer().sign(get);

            String response = client.execute(get, new BasicResponseHandler());

            return new JSONArray(response);
        }
        catch (Exception e)
        {
            Log.e("ScrollLock", e.getClass().toString(), e);
        }

        return null;
    }

    protected void onPostExecute(JSONArray jsonArray)
    {
        if(jsonArray == null)
            return;
        try
        {
            ContentValues[] tweets = new ContentValues[jsonArray.length()];
            long lastTweetId = 0;
            for(int i = 0; i < jsonArray.length(); ++i)
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

                lastTweetId = tweet.getTweetId();
            }
            settingsManager.setTweetSinceId(lastTweetId);
            context.getContentResolver().bulkInsert(TweetProvider.TWEET_URI, tweets);
        }
        catch (JSONException e)
        {
            Log.e("ScrollLock", e.getClass().toString(), e);
        }
    }
}