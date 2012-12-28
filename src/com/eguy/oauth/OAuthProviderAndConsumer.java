package com.eguy.oauth;

import com.eguy.ConsumerInfo;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

public class OAuthProviderAndConsumer
{
    public final String CALLBACK_URL = "scrolllock://callback";

    OAuthProvider provider = new DefaultOAuthProvider(
            "https://api.twitter.com/oauth/request_token",
            "https://api.twitter.com/oauth/access_token",
            "https://api.twitter.com/oauth/authorize");

    OAuthConsumer consumer;

    public OAuthProviderAndConsumer(AuthCredentialManager credentialManager)
    {
        consumer = new CommonsHttpOAuthConsumer(ConsumerInfo.CONSUMER_KEY, ConsumerInfo.CONSUMER_SECRET);

        String userToken = credentialManager.getUserToken();
        String userTokenSecret = credentialManager.getUserTokenSecret();

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