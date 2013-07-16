package dev.emmaguy.twitterclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import dev.emmaguy.twitterclient.R;
import dev.emmaguy.twitterclient.SettingsManager;
import dev.emmaguy.twitterclient.authentication.SignInFragment;
import dev.emmaguy.twitterclient.authentication.SignInFragment.OnSignInCompleteListener;
import dev.emmaguy.twitterclient.timeline.TimelineFragment;

public class MainActivity extends SherlockFragmentActivity implements OnSignInCompleteListener, ActionBar.TabListener {

    private SettingsManager settingsManager;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager pager;

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_main);

	ViewPager.SimpleOnPageChangeListener ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
	    @Override
	    public void onPageSelected(int position) {
		super.onPageSelected(position);
		getSupportActionBar().setSelectedNavigationItem(position);
	    }
	};

	settingsManager = new SettingsManager(this.getApplicationContext());
	viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this, settingsManager);
	
	pager = (ViewPager) findViewById(R.id.pager);
	pager.setOnPageChangeListener(ViewPagerListener);
	pager.setAdapter(viewPagerAdapter);

	getLatestTweetsOrAuthenticate();
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
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