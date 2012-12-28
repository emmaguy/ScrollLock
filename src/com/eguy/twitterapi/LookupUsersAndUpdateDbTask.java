package com.eguy.twitterapi;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.eguy.oauth.AuthCredentialManager;
import com.eguy.ui.TimelineActivity;
import com.eguy.db.SavedUser;
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

public class LookupUsersAndUpdateDbTask extends AsyncTask<Void, Void, JSONArray>
{
    private final String USER_LOOKUP_URL = "https://api.twitter.com/1/users/lookup.json";
    HttpClient client;
    private Iterable<Long> userIds;
    private OAuthProviderAndConsumer producerAndConsumer;
    private AuthCredentialManager credentialManager;
    private TweetDatabase tweetDatabase;
    private TimelineActivity timelineActivity;

    public LookupUsersAndUpdateDbTask(Iterable<Long> userIds, OAuthProviderAndConsumer producerAndConsumer, AuthCredentialManager credentialManager, TweetDatabase tweetDatabase, TimelineActivity timelineActivity)
    {
        this.userIds = userIds;
        this.producerAndConsumer = producerAndConsumer;
        this.credentialManager = credentialManager;
        this.tweetDatabase = tweetDatabase;
        this.timelineActivity = timelineActivity;

        client = new HttpClientBuilder().Builder();
    }

    @Override
    protected JSONArray doInBackground(Void... arg0)
    {
        try
        {
            Uri sUri = Uri.parse(USER_LOOKUP_URL);
            Uri.Builder builder = sUri.buildUpon();
            StringBuilder usersToLooup = new StringBuilder();
            for(Long l : userIds)
            {
                usersToLooup.append(l);
                usersToLooup.append(",");
            }

            String ids = usersToLooup.toString();
            String usersnamesWithoutTrailingComma = ids.substring(0, ids.length() - 1);
            builder.appendQueryParameter("user_id", usersnamesWithoutTrailingComma);
            builder.appendQueryParameter("include_entities", "0");

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
            List<SavedUser> users = new ArrayList<SavedUser>();
            for(int i = 0; i < jsonArray.length(); ++i)
            {
                JSONObject status = jsonArray.getJSONObject(i);
                JsonUser user = new JsonUser(status);
                SavedUser savedUser = new SavedUser(user.getUserId(), user.getUsername(), user.getProfilePicUrl());
                users.add(savedUser);
            }

            tweetDatabase.saveUsers(users);
            timelineActivity.refreshListView();
        }
        catch (JSONException e)
        {
            Log.e("ScrollLock", e.getClass().toString(), e);
        }
    }
}
