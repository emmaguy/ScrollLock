package com.eguy.oauth;

import com.eguy.ConsumerInfo;
import com.eguy.IContainSettings;
import com.eguy.SettingsManager;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

public class OAuthProviderAndConsumer
{
    public final String CALLBACK_URL = "scrolllock://callback";

    private OAuthConsumer consumer;
    private OAuthProvider provider = new DefaultOAuthProvider(
                                        "https://api.twitter.com/oauth/request_token",
                                        "https://api.twitter.com/oauth/access_token",
                                        "https://api.twitter.com/oauth/authorize");


    public OAuthProviderAndConsumer(IContainSettings settingsManager)
    {
        consumer = new CommonsHttpOAuthConsumer(ConsumerInfo.CONSUMER_KEY, ConsumerInfo.CONSUMER_SECRET);

        String userToken = settingsManager.getUserToken();
        String userTokenSecret = settingsManager.getUserTokenSecret();

        if(!userToken.isEmpty() && !userTokenSecret.isEmpty())
            consumer.setTokenWithSecret(userToken, userTokenSecret);
    }

    public OAuthConsumer getConsumer()
    {
        return consumer;
    }

    public OAuthProvider getProvider()
    {
        return provider;
    }
}