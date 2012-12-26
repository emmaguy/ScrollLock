package com.eguy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import junit.framework.Assert;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpParameters;

public class AuthenticateActivity extends Activity
{
    private OAuthProviderAndConsumer oAuthProviderAndConsumer;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        final Context context = getApplicationContext();

        oAuthProviderAndConsumer = new OAuthProviderAndConsumer(new AuthCredentialManager(this.getApplicationContext()));

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new TwitterOAuthLoadAuthUrlTask().execute(context, oAuthProviderAndConsumer);
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Uri uri = this.getIntent().getData();
        if (uri != null && uri.toString().startsWith(oAuthProviderAndConsumer.CALLBACK_URL))
        {
            retrieveAccessTokenFromRequestToken(uri);
        }
    }

    private void retrieveAccessTokenFromRequestToken(Uri uri)
    {
        AuthCredentialManager credentialManager = new AuthCredentialManager(this.getApplicationContext());
        String token = credentialManager.getToken();
        String secret = credentialManager.getTokenSecret();

        OAuthConsumer consumer = oAuthProviderAndConsumer.getConsumer();
        OAuthProvider provider = oAuthProviderAndConsumer.getProvider();

        consumer.setTokenWithSecret(token, secret);
        try
        {
            Assert.assertEquals(uri.getQueryParameter(OAuth.OAUTH_TOKEN), consumer.getToken());

            provider.retrieveAccessToken(consumer, uri.getQueryParameter(OAuth.OAUTH_VERIFIER));
            credentialManager.saveUserTokenAndSecret(consumer.getToken(), consumer.getTokenSecret());

            HttpParameters responseParameters = provider.getResponseParameters();
            String userName = responseParameters.getFirst("screen_name");
            String userId = responseParameters.getFirst("user_id");

            credentialManager.saveUsernameAndUserId(userName, userId);

            startActivity(new Intent(this, TimelineActivity.class));
        }
        catch (OAuthMessageSignerException e)
        {
            Log.e("blah", "OAuthMessageSignerException", e);
        }
        catch (OAuthNotAuthorizedException e)
        {
            Log.e("blah", "OAuthNotAuthorizedException", e);
        }
        catch (OAuthExpectationFailedException e)
        {
            Log.e("blah", "OAuthNotAuthorizedException", e);
        }
        catch (OAuthCommunicationException e)
        {
            Log.e("blah", "OAuthCommunicationException", e);
        }
    }
}