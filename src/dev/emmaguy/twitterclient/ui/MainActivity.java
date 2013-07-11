package dev.emmaguy.twitterclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import dev.emmaguy.twitterclient.R;
import dev.emmaguy.twitterclient.SettingsManager;
import dev.emmaguy.twitterclient.authentication.SignInFragment;
import dev.emmaguy.twitterclient.authentication.SignInFragment.OnSignInCompleteListener;
import dev.emmaguy.twitterclient.timeline.TimelineFragment;
import dev.emmaguy.twitterclient.timeline.TimelineFragment.OnUserActionListener;
import dev.emmaguy.twitterclient.timeline.details.TweetDetailsFragment;

public class MainActivity extends FragmentActivity implements OnSignInCompleteListener, OnUserActionListener {

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_main);

	getLatestTweetsOrAuthenticate();
    }

    private void getLatestTweetsOrAuthenticate() {
	SettingsManager settingsManager = new SettingsManager(this.getApplicationContext());
	if (settingsManager.credentialsAvailable()) {
	    showTimeline();
	} else {
	    SignInFragment fragment = new SignInFragment();

	    FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
	    transaction.replace(R.id.fragment_container, fragment);
	    transaction.addToBackStack(null);
	    transaction.commit();
	}
    }

    private void showTimeline() {
	TimelineFragment fragment = new TimelineFragment();

	FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
	transaction.replace(R.id.fragment_container, fragment);
	transaction.addToBackStack(null);
	transaction.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onSignInComplete() {
	showTimeline();
    }

    @Override
    public void onOpenTweetDetails(String tweetText, long userId) {
	final TimelineFragment timelineFragment = (TimelineFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
	
	TweetDetailsFragment tweetDetailsFragment = new TweetDetailsFragment();
	tweetDetailsFragment.setTweetText(tweetText);

	FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
	transaction.hide(timelineFragment);
	transaction.add(R.id.fragment_container, tweetDetailsFragment);
	transaction.addToBackStack(null);
	transaction.commit();
    }
}