package com.eguy;

public class TwitterRequests
{
    private AuthCredentialManager credentialManager;
    private OAuthProviderAndConsumer producerAndConsumer;

    public TwitterRequests(AuthCredentialManager credentialManager, OAuthProviderAndConsumer producerAndConsumer)
    {
        this.credentialManager = credentialManager;
        this.producerAndConsumer = producerAndConsumer;
    }

    public void LoadTweets()
    {
        new LoadTweetsTask(producerAndConsumer,  credentialManager).execute();
    }
}
