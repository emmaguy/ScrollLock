package dev.emmaguy.twitterclient.ui;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import dev.emmaguy.twitterclient.IContainSettings;
import dev.emmaguy.twitterclient.R;
import dev.emmaguy.twitterclient.SettingsManager;
import dev.emmaguy.twitterclient.authentication.SignInFragment;
import dev.emmaguy.twitterclient.authentication.SignInFragment.OnSignInCompleteListener;
import dev.emmaguy.twitterclient.timeline.TimelineFragment;

public class MainActivity extends SherlockFragmentActivity implements OnSignInCompleteListener,
	IContainPullToRefreshAttacher {

    private IContainSettings settingsManager;
    private TimelinesViewPagerAdapter viewPagerAdapter;
    private ViewPager pager;
    private PullToRefreshAttacher pullToRefreshHelper;

    public void onCreate(Bundle savedInstanceState) {
	settingsManager = new SettingsManager(this.getApplicationContext());
	setTheme(settingsManager.getThemeResourceId());
	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_main);

	pullToRefreshHelper = new PullToRefreshAttacher(this);
	viewPagerAdapter = new TimelinesViewPagerAdapter(getSupportFragmentManager());

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

		TimelineFragment t = (TimelineFragment) viewPagerAdapter.getRegisteredFragment(pager.getCurrentItem());
		pullToRefreshHelper.setRefreshableView(t.getListView(), t);
	    }
	};

	pager = (ViewPager) findViewById(R.id.pager);
	pager.setOffscreenPageLimit(viewPagerAdapter.getCount());
	pager.setOnPageChangeListener(viewPagerListener);
	pager.setAdapter(viewPagerAdapter);
    }

    private void initialiseActionBar() {
	final ActionBar actionBar = getSupportActionBar();
	actionBar.setDisplayShowTitleEnabled(false);
	actionBar.setDisplayShowCustomEnabled(true);
	actionBar.setDisplayHomeAsUpEnabled(false);
	actionBar.setDisplayShowHomeEnabled(false);
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
	    pager.setCurrentItem(TimelineFragment.HOME_TIMELINE);
	    break;
	case R.id.mentions_button:
	    pager.setCurrentItem(TimelineFragment.MENTIONS_TIMELINE);
	    break;
	case R.id.dms_button:
	    pager.setCurrentItem(TimelineFragment.DIRECTS_TIMELINE);
	    break;
	case android.R.id.home:
	    ifTweetDetailsFragmentIsShowingMoveBackToTimeline();
	    break;
	case R.id.settings_button:
	    Intent intent = new Intent(this, AccountPreferencesActivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
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
	    initialiseActionBar();
	    return true;
	}
	return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
	switch (pager.getCurrentItem()) {
	case TimelineFragment.HOME_TIMELINE:
	    menu.findItem(R.id.home_timeline_button).setIcon(R.drawable.home_selected);
	    break;
	case TimelineFragment.MENTIONS_TIMELINE:
	    menu.findItem(R.id.mentions_button).setIcon(R.drawable.mentions_selected);
	    break;
	case TimelineFragment.DIRECTS_TIMELINE:
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
	// remove SignInFragment
	FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
	transaction.remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
	transaction.commit();
	
	pager.setCurrentItem(TimelineFragment.HOME_TIMELINE);

	TimelineFragment t = (TimelineFragment) viewPagerAdapter.getRegisteredFragment(pager.getCurrentItem());
	t.refresh();
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

    @Override
    public PullToRefreshAttacher getRefreshAttacher() {
	return pullToRefreshHelper;
    }
}