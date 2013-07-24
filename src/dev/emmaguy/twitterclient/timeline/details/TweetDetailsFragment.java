package dev.emmaguy.twitterclient.timeline.details;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;

import dev.emmaguy.twitterclient.R;
import dev.emmaguy.twitterclient.R.color;
import dev.emmaguy.twitterclient.ui.TweetDateFormatter;

@SuppressLint("SetJavaScriptEnabled")
public class TweetDetailsFragment extends SherlockFragment {
    
    private String tweetText;
    private String tweetCreatedAt;
    private String tweetUserUsername;
    private final TweetDateFormatter tweetDateFormatter = new TweetDateFormatter();
    private String avatarUrl;

    public void setTweet(String tweetText, String tweetCreatedAt, String tweetUserUsername, String avatarUrl) {
	this.tweetText = tweetText;
	this.tweetCreatedAt = tweetCreatedAt;
	this.tweetUserUsername = tweetUserUsername;
	this.avatarUrl = avatarUrl;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	setHasOptionsMenu(true);
	return inflater.inflate(R.layout.fragment_tweet_details, null);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//	switch (item.getItemId()) {
//	case R.id.back_button:
//	    getActivity().onBackPressed();
	    return true;
//	default:
//	    return super.onOptionsItemSelected(item);
//	}
    }
    
    @Override
    public void onStart() {
	super.onStart();
	
	TextView tweetTextView = (TextView) getActivity().findViewById(R.id.tweet_text_details_textview);
	tweetTextView.setText(tweetText);
	
	TextView usernameTextView = (TextView) getActivity().findViewById(R.id.details_username_textview);
	usernameTextView.setText("@" + tweetUserUsername);
	
	TextView timestampTextView = (TextView) getActivity().findViewById(R.id.details_timestamp_textview);
	timestampTextView.setText(tweetDateFormatter.getFormattedDateTime(tweetCreatedAt));
	
	ImageView avatarImageView = (ImageView) getActivity().findViewById(R.id.avatar_imageview);
	Picasso.with(getActivity()).load(avatarUrl).into(avatarImageView);

	initialiseWebView();
    }

    private void initialiseWebView() {
	final ProgressBar webViewProgressBar = (ProgressBar) getActivity().findViewById(R.id.page_load_progress_bar);
	
	Pattern p = Pattern.compile(String.valueOf(Patterns.WEB_URL));
	Matcher m = p.matcher(tweetText);

	List<String> urls = new ArrayList<String>();
	while (m.find()) {
	    urls.add(m.group());
	}

	if (!urls.isEmpty()) {
	    WebView webView = (WebView) getActivity().findViewById(R.id.webView);
	    webView.setWebViewClient(new WebViewClient() {
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		    handler.proceed();
		}
	    });
	    webView.setWebChromeClient(new WebChromeClient() {
		    public void onProgressChanged(WebView view, int progress) {
			if (progress < 100 && webViewProgressBar.getVisibility() == ProgressBar.GONE) {
			    webViewProgressBar.setVisibility(ProgressBar.VISIBLE);
			}
			webViewProgressBar.setProgress(progress);
			if (progress == 100) {
			    webViewProgressBar.setVisibility(ProgressBar.GONE);
			}
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
    }
}