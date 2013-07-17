package dev.emmaguy.twitterclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import dev.emmaguy.twitterclient.R;
import dev.emmaguy.twitterclient.SettingsManager;
import dev.emmaguy.twitterclient.authentication.SignInFragment;
import dev.emmaguy.twitterclient.authentication.SignInFragment.OnSignInCompleteListener;
import dev.emmaguy.twitterclient.timeline.TimelineFragment;

public class MainActivity extends SherlockFragmentActivity implements OnSignInCompleteListener {

    private SettingsManager settingsManager;
    private TimelinesViewPagerAdapter viewPagerAdapter;
    private ViewPager pager;

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_main);

	settingsManager = new SettingsManager(this.getApplicationContext());
	viewPagerAdapter = new TimelinesViewPagerAdapter(getSupportFragmentManager(), this, settingsManager);

	initialiseActionBar();
	initialiseViewPager();

	if (!settingsManager.credentialsAvailable()) {
	    signInUser();
	}
    }

    private void signInUser() {
	SignInFragment fragment = new SignInFragment();

	FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
	transaction.replace(R.id.fragment_container, fragment);
	transaction.addToBackStack(null);
	transaction.commit();
    }

    private void initialiseViewPager() {
	ViewPager.SimpleOnPageChangeListener viewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
	    @Override
	    public void onPageSelected(int position) {
		super.onPageSelected(position);

		// the user can click on a tweet to view details - a webview etc
		// if the user navigates away, ensure we are at the top of the
		// backstack and are viewing the timeline, not a child fragment
		ifTweetDetailsFragmentIsShowingMoveBackToTimeline();
	    }
	};

	pager = (ViewPager) findViewById(R.id.pager);
	pager.setOnPageChangeListener(viewPagerListener);
	pager.setAdapter(viewPagerAdapter);
    }

    private void initialiseActionBar() {
	final ActionBar actionBar = getSupportActionBar();
	actionBar.setDisplayShowHomeEnabled(false);
	actionBar.setDisplayShowTitleEnabled(false);
	actionBar.setDisplayShowCustomEnabled(true);
	actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getSupportMenuInflater();
	inflater.inflate(R.menu.menu_timeline, menu);
	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
	switch (menuItem.getItemId()) {
	case R.id.home_timeline_button:
	    pager.setCurrentItem(TimelinesViewPagerAdapter.HOME_TIMELINE);
	    break;
	case R.id.mentions_button:
	    pager.setCurrentItem(TimelinesViewPagerAdapter.MENTIONS_TIMELINE);
	    break;
	case R.id.dms_button:
	    pager.setCurrentItem(TimelinesViewPagerAdapter.DIRECTS_TIMELINE);
	    break;
	case R.id.refresh_button:
	    TimelineFragment f = (TimelineFragment) viewPagerAdapter.getRegisteredFragment(pager.getCurrentItem());
	    f.refresh();
	    break;
	case R.id.back_button:
	    ifTweetDetailsFragmentIsShowingMoveBackToTimeline();
	    break;
	default:
	    return super.onOptionsItemSelected(menuItem);
	}
	return true;
    }

    private boolean ifTweetDetailsFragmentIsShowingMoveBackToTimeline() {
	TimelineFragment t = (TimelineFragment) viewPagerAdapter.getRegisteredFragment(pager.getCurrentItem());
	if (t.isShowingTweetDetailsFragment()) {
	    t.getChildFragmentManager().popBackStackImmediate();
	    return true;
	}
	return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

	TimelineFragment f = (TimelineFragment) viewPagerAdapter.getRegisteredFragment(pager.getCurrentItem());
	menu.findItem(R.id.back_button).setVisible(f.isShowingTweetDetailsFragment());

	switch (pager.getCurrentItem()) {
	case TimelinesViewPagerAdapter.HOME_TIMELINE:
	    menu.findItem(R.id.home_timeline_button).setIcon(R.drawable.home_selected);
	    break;
	case TimelinesViewPagerAdapter.MENTIONS_TIMELINE:
	    menu.findItem(R.id.mentions_button).setIcon(R.drawable.mentions_selected);
	    break;
	case TimelinesViewPagerAdapter.DIRECTS_TIMELINE:
	    menu.findItem(R.id.dms_button).setIcon(R.drawable.message_selected);
	    break;
	}

	return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
	super.onNewIntent(intent);
	setIntent(intent);
    }

    @Override
    public void onSignInComplete() {

    }

    @Override
    public void onBackPressed() {
	if (!ifTweetDetailsFragmentIsShowingMoveBackToTimeline()) {
	    super.onBackPressed();
	}
    }

    public interface BackButtonPressedListener {
	boolean onBackButtonPressed();
    }
}