package dev.emmaguy.twitterclient.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import dev.emmaguy.twitterclient.R;
import dev.emmaguy.twitterclient.R.color;

@SuppressLint("SetJavaScriptEnabled")
public class TweetDetailsFragment extends Fragment {
    
    private String tweetText;

    public void setTweetText(String tweetText) {
	this.tweetText = tweetText;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	final View v = inflater.inflate(R.layout.fragment_tweet_details, null);

	TextView tweetTextView = (TextView) v.findViewById(R.id.tweet);
	tweetTextView.setText(tweetText);

	Pattern p = Pattern.compile(String.valueOf(Patterns.WEB_URL));
	Matcher m = p.matcher(tweetText);

	List<String> urls = new ArrayList<String>();
	while (m.find()) {
	    urls.add(m.group());
	}

	if (!urls.isEmpty()) {
	    WebView webView = (WebView) v.findViewById(R.id.webView);
	    webView.setWebViewClient(new WebViewClient() {
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		    handler.proceed();
		}
	    });
	    webView.getSettings().setJavaScriptEnabled(true);
	    webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
	    webView.getSettings().setBuiltInZoomControls(true);
	    webView.getSettings().setDomStorageEnabled(true);
	    webView.getSettings().setLoadWithOverviewMode(true);
	    webView.getSettings().setUseWideViewPort(true);
	    webView.setBackgroundColor(0);
	    webView.setBackgroundResource(color.BlanchedAlmond);
	    webView.loadUrl(urls.get(0));
	}
	
	return v;
    }
}