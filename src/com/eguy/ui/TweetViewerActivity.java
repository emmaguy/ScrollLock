package com.eguy.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.eguy.R;
import com.eguy.R.color;
import com.eguy.db.TweetProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TweetViewerActivity extends Activity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tweet_viewer);

        Intent i = getIntent();

        TextView tweetText = (TextView) findViewById(R.id.tweet);
        String tweetContents = i.getStringExtra(TweetProvider.TWEET_TEXT);
        tweetText.setText(tweetContents);

        Pattern p = Pattern.compile(String.valueOf(Patterns.WEB_URL));
        Matcher m = p.matcher(tweetContents);

        List<String> urls = new ArrayList<String>();
        while (m.find())
        {
            urls.add(m.group());
        }

        if (!urls.isEmpty())
        {
            WebView webView = (WebView) findViewById(R.id.webView);
            webView.setWebViewClient(new WebViewClient()
            {
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
                {
                    handler.proceed();
                }
            }
            );
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setBackgroundColor(0);
            webView.setBackgroundResource(color.BlanchedAlmond);
            webView.loadUrl(urls.get(0));
        }
    }
}