package com.eguy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class AuthenticateActivity extends Activity
{
   @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        final Context context = getApplicationContext();

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new TwitterOAuthLoadAuthUrlTask().execute(context);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Log.d("infoo", "intent!");
    }

//
//    @Override
//    public void onResume()
//    {
//        Uri uri = this.getIntent().getData();
//        if (uri != null && uri.toString().startsWith(CALLBACK_URL))
//        {
//            String verifier = uri.getQueryParameter("oauth_verifier");
//            // this will populate token and token_secret in consumer
//            try
//            {
//                provider.retrieveAccessToken(consumer, verifier);
//            } catch (OAuthMessageSignerException e)
//            {
//                Log.e("AuthenticateActivity", e.getMessage());
//            } catch (OAuthNotAuthorizedException e)
//            {
//                Log.e("AuthenticateActivity", e.getMessage());
//            } catch (OAuthExpectationFailedException e)
//            {
//                Log.e("AuthenticateActivity", e.getMessage());
//            } catch (OAuthCommunicationException e)
//            {
//                Log.e("AuthenticateActivity", e.getMessage());
//            }
//        }
//    }
}
