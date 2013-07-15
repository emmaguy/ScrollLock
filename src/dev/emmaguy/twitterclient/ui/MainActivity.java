package dev.emmaguy.twitterclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import dev.emmaguy.twitterclient.R;
import dev.emmaguy.twitterclient.SettingsManager;
import dev.emmaguy.twitterclient.authentication.SignInFragment;
import dev.emmaguy.twitterclient.authentication.SignInFragment.OnSignInCompleteListener;
import dev.emmaguy.twitterclient.db.TweetProvider;
import dev.emmaguy.twitterclient.db.TweetStorer;
import dev.emmaguy.twitterclient.timeline.DMsTimelineTweetRequester;
import dev.emmaguy.twitterclient.timeline.HomeTimelineTweetRequester;
import dev.emmaguy.twitterclient.timeline.MentionsTimelineTweetRequester;
import dev.emmaguy.twitterclient.timeline.TimelineAdapter;
import dev.emmaguy.twitterclient.timeline.TimelineFragment;
import dev.emmaguy.twitterclient.timeline.TimelineFragment.OnUserActionListener;
import dev.emmaguy.twitterclient.timeline.details.TweetDetailsFragment;

public class MainActivity extends SherlockFragmentActivity implements OnSignInCompleteListener, OnUserActionListener,
	ActionBar.TabListener {

    private SettingsManager settingsManager;

    private TimelineFragment homeTimelineFragment;
    private TimelineFragment mentionsFragment;
    private TimelineFragment dmsFragment;

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_main);
	settingsManager = new SettingsManager(this.getApplicationContext());

	homeTimelineFragment = new TimelineFragment();
	homeTimelineFragment.setArguments(settingsManager, new HomeTimelineTweetRequester(), new TweetStorer(
		getContentResolver(), getSupportLoaderManager(), new TimelineAdapter(this, null),
		TweetProvider.TWEET_HOME_TIMELINE_URI, getApplicationContext()));

	mentionsFragment = new TimelineFragment();
	mentionsFragment.setArguments(settingsManager, new MentionsTimelineTweetRequester(), new TweetStorer(
		getContentResolver(), getSupportLoaderManager(), new TimelineAdapter(this, null),
		TweetProvider.TWEET_MENTIONS_TIMELINE_URI, getApplicationContext()));

	dmsFragment = new TimelineFragment();
	dmsFragment.setArguments(settingsManager, new DMsTimelineTweetRequester(), new TweetStorer(
		getContentResolver(), getSupportLoaderManager(), new TimelineAdapter(this, null),
		TweetProvider.TWEET_DMS_TIMELINE_URI, getApplicationContext()));

	getLatestTweetsOrAuthenticate();
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	if (tab.getPosition() == 0) {
	    ft.add(R.id.fragment_container, homeTimelineFragment);
	    ft.attach(homeTimelineFragment);
	} else if (tab.getPosition() == 1) {
	    ft.add(R.id.fragment_container, mentionsFragment);
	    ft.attach(mentionsFragment);
	} else if (tab.getPosition() == 2) {
	    ft.add(R.id.fragment_container, dmsFragment);
	    ft.attach(dmsFragment);
	}
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	if (tab.getPosition() == 0) {
	    ft.remove(homeTimelineFragment);
	} else if (tab.getPosition() == 1) {
	    ft.remove(mentionsFragment);
	} else if (tab.getPosition() == 2) {
	    ft.remove(dmsFragment);	
	}
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    private void getLatestTweetsOrAuthenticate() {

	if (settingsManager.credentialsAvailable()) {
	    showHomeTimeline();
	} else {
	    SignInFragment fragment = new SignInFragment();

	    FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
	    transaction.replace(R.id.fragment_container, fragment);
	    transaction.addToBackStack(null);
	    transaction.commit();
	}
    }

    private void showHomeTimeline() {

	ActionBar bar = getSupportActionBar();
	bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	ActionBar.Tab tabHome = bar.newTab();
	ActionBar.Tab tabMentions = bar.newTab();
	ActionBar.Tab tabDms = bar.newTab();

	tabHome.setText("Home");
	tabMentions.setText("Mentions");
	tabDms.setText("Directs");

	tabHome.setTabListener(this);
	tabMentions.setTabListener(this);
	tabDms.setTabListener(this);

	bar.addTab(tabHome);
	bar.addTab(tabMentions);
	bar.addTab(tabDms);
    }

    @Override
    protected void onNewIntent(Intent intent) {
	super.onNewIntent(intent);
	setIntent(intent);
    }

    @Override
    public void onSignInComplete() {
	showHomeTimeline();
    }

    @Override
    public void onOpenTweetDetails(String tweetText, long userId) {
	final TimelineFragment timelineFragment = (TimelineFragment) getSupportFragmentManager().findFragmentById(
		R.id.fragment_container);

	TweetDetailsFragment tweetDetailsFragment = new TweetDetailsFragment();
	tweetDetailsFragment.setTweetText(tweetText);

	FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
	transaction.hide(timelineFragment);
	transaction.add(R.id.fragment_container, tweetDetailsFragment);
	transaction.addToBackStack(null);
	transaction.commit();
    }
}