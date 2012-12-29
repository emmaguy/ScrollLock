package com.eguy.twitterapi;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import com.eguy.SettingsManager;
import com.eguy.db.ProcessedTweet;
import com.eguy.db.TweetDatabase;
import com.eguy.oauth.OAuthProviderAndConsumer;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoadTweetsAndUpdateDbTask extends AsyncTask<Void, Void, JSONArray>
{
    private final String HOME_TIMELINE_URL = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    private OAuthProviderAndConsumer producerAndConsumer;
    private SettingsManager settingsManager;
    private TweetDatabase tweetDatabase;
    private HttpClient client;

    public LoadTweetsAndUpdateDbTask(OAuthProviderAndConsumer producerAndConsumer, SettingsManager settingsManager, TweetDatabase tweetDatabase)
    {
        this.producerAndConsumer = producerAndConsumer;
        this.settingsManager = settingsManager;
        this.tweetDatabase = tweetDatabase;

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
            builder.appendQueryParameter("count", "200");

            if(settingsManager.getTweetSinceId() != 0)
            {
                builder.appendQueryParameter("since_id", String.valueOf(settingsManager.getTweetSinceId()));
            }

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
            List<ProcessedTweet> tweets = new ArrayList<ProcessedTweet>();
            for(int i = 0; i < jsonArray.length(); ++i)
            {
                JSONObject status = jsonArray.getJSONObject(i);
                JsonTweet tweet = new JsonTweet(status);
                ProcessedTweet savedTweet = new ProcessedTweet(tweet.getTweetId(), tweet.getText(), tweet.getTweetCreatedAt(), tweet.getUserId(), tweet.getUsername(), tweet.getProfilePicUrl());
                tweets.add(savedTweet);
            }
            settingsManager.setTweetSinceId(tweets.get(tweets.size() - 1).getTweetId());
            tweetDatabase.saveTweets(tweets);

            //Toast.makeText(, "done getting tweets", Toast.LENGTH_SHORT).show();
        }
        catch (JSONException e)
        {
            Log.e("ScrollLock", e.getClass().toString(), e);
        }
    }
}