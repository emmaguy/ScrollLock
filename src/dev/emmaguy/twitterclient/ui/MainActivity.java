package dev.emmaguy.twitterclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

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
	
	getSupportActionBar().setDisplayShowHomeEnabled(false);
	getSupportActionBar().setDisplayShowTitleEnabled(false);

	ViewPager.SimpleOnPageChangeListener viewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
	    @Override
	    public void onPageSelected(int position) {
		super.onPageSelected(position);
		
		// the user can click on a tweet to view details - a webview etc
		// if the user navigates away, ensure we are at the top of the backstack and are viewing the timeline, not a child fragment
		TimelineFragment f = (TimelineFragment)viewPagerAdapter.getRegisteredFragment(pager.getCurrentItem());
		f.onBackButtonPressed();
	    }
	};

	settingsManager = new SettingsManager(this.getApplicationContext());
	viewPagerAdapter = new TimelinesViewPagerAdapter(getSupportFragmentManager(), this, settingsManager);
	
	pager = (ViewPager) findViewById(R.id.pager);
	pager.setOnPageChangeListener(viewPagerListener);
	pager.setAdapter(viewPagerAdapter);

	getLatestTweetsOrAuthenticate();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getSupportMenuInflater();
	inflater.inflate(R.menu.menu_timeline, menu);
	super.onCreateOptionsMenu(menu);
	
	return true;
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
	    TimelineFragment f = (TimelineFragment)viewPagerAdapter.getRegisteredFragment(pager.getCurrentItem());
	    f.refresh();
	    break;
	}
	return super.onOptionsItemSelected(menuItem);
    }

    private void getLatestTweetsOrAuthenticate() {

	if (settingsManager.credentialsAvailable()) {
	    buildTabsUiWithActionBar();
	} else {
	    SignInFragment fragment = new SignInFragment();

	    FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
	    transaction.replace(R.id.fragment_container, fragment);
	    transaction.addToBackStack(null);
	    transaction.commit();
	}
    }

    private void buildTabsUiWithActionBar() {


    }

    @Override
    protected void onNewIntent(Intent intent) {
	super.onNewIntent(intent);
	setIntent(intent);
    }

    @Override
    public void onSignInComplete() {
	buildTabsUiWithActionBar();
    }

    @Override
    public void onBackPressed() {
       TimelineFragment f = (TimelineFragment)viewPagerAdapter.getRegisteredFragment(pager.getCurrentItem());
       if(!f.onBackButtonPressed()){
	   super.onBackPressed();
       }
    }
    
    public interface BackButtonPressedListener {
	boolean onBackButtonPressed();
    }
}