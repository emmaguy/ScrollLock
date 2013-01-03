package com.eguy.twitterapi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.eguy.SettingsManager;
import com.eguy.oauth.OAuthProviderAndConsumer;

public class EnsureThereAreNoGapsInTimelineTask extends AsyncTask<Void, Void, Void>
{
    private long lastTweetId;
    private SettingsManager settingsManager;
    private OAuthProviderAndConsumer producerAndConsumer;
    private Context context;

    public EnsureThereAreNoGapsInTimelineTask(long lastTweetId, SettingsManager settingsManager, OAuthProviderAndConsumer producerAndConsumer, Context context)
    {
        this.lastTweetId = lastTweetId;
        this.settingsManager = settingsManager;
        this.producerAndConsumer = producerAndConsumer;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        if(settingsManager.getTweetMaxId() < lastTweetId)
        {
            new LoadTweetsAndUpdateDbTask(producerAndConsumer, settingsManager, context, 0, lastTweetId, 200).execute();
            settingsManager.setTweetMaxId(lastTweetId);
        }
        else
        {
            settingsManager.setTweetSinceId(lastTweetId);
        }

        return null;
    }
}
