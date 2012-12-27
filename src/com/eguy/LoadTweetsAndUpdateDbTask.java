package com.eguy;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.eguy.db.SavedTweet;
import com.eguy.db.TweetDatabase;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoadTweetsAndUpdateDbTask extends AsyncTask<Void, Void, JSONArray>
{
    private final String USER_TIMELINE_URL = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    HttpClient client;
    private OAuthProviderAndConsumer producerAndConsumer;
    private AuthCredentialManager credentialManager;
    private TweetDatabase tweetDatabase;
    private TimelineActivity timelineActivity;

    public LoadTweetsAndUpdateDbTask(OAuthProviderAndConsumer producerAndConsumer, AuthCredentialManager credentialManager, TweetDatabase tweetDatabase, TimelineActivity timelineActivity)
    {
        this.producerAndConsumer = producerAndConsumer;
        this.credentialManager = credentialManager;
        this.tweetDatabase = tweetDatabase;
        this.timelineActivity = timelineActivity;

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        HttpParams params = new BasicHttpParams();
        SingleClientConnManager mgr = new SingleClientConnManager(params, schemeRegistry);

        client = new DefaultHttpClient(mgr, params);
    }

    @Override
    protected JSONArray doInBackground(Void... arg0)
    {
        try
        {
            Uri sUri = Uri.parse(USER_TIMELINE_URL);
            Uri.Builder builder = sUri.buildUpon();
            builder.appendQueryParameter("screen_name", credentialManager.getUsername());
            builder.appendQueryParameter("count", "200");

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
            List<SavedTweet> tweets = new ArrayList<SavedTweet>();
            for(int i = 0; i < jsonArray.length(); ++i)
            {
                JSONObject status = jsonArray.getJSONObject(i);
                Tweet tweet = new Tweet(status);
                SavedTweet savedTweet = new SavedTweet(tweet.getTweetText(), tweet.getTweetCreatedAt(), tweet.getTweetUserId());
                tweets.add(savedTweet);
            }

            tweetDatabase.saveTweets(tweets);
        }
        catch (JSONException e)
        {
            Log.e("ScrollLock", e.getClass().toString(), e);
        }

        timelineActivity.refreshListView();
    }
}