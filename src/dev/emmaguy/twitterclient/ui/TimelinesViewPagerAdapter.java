package dev.emmaguy.twitterclient.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import dev.emmaguy.twitterclient.IContainSettings;
import dev.emmaguy.twitterclient.db.TweetProvider;
import dev.emmaguy.twitterclient.db.TweetStorer;
import dev.emmaguy.twitterclient.timeline.DMsTimelineTweetRequester;
import dev.emmaguy.twitterclient.timeline.HomeTimelineTweetRequester;
import dev.emmaguy.twitterclient.timeline.MentionsTimelineTweetRequester;
import dev.emmaguy.twitterclient.timeline.TimelineAdapter;
import dev.emmaguy.twitterclient.timeline.TimelineFragment;

public class TimelinesViewPagerAdapter extends FragmentPagerAdapter {

    public static final int HOME_TIMELINE = 0;
    public static final int MENTIONS_TIMELINE = 1;
    public static final int DIRECTS_TIMELINE = 2;
    
    private static final int PAGE_COUNT = 3;
    private final SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private FragmentActivity activity;
    private IContainSettings settingsManager;

    public TimelinesViewPagerAdapter(FragmentManager fm, final FragmentActivity activity, IContainSettings settingsManager) {
	super(fm);
	this.activity = activity;
	this.settingsManager = settingsManager;
    }

    @Override
    public Fragment getItem(int arg0) {
	switch (arg0) {

	case HOME_TIMELINE:
	    TimelineFragment homeFragment = new TimelineFragment();
	    homeFragment.setArguments(settingsManager, new HomeTimelineTweetRequester(),
		    new TweetStorer(activity.getContentResolver(), activity.getSupportLoaderManager(),
			    new TimelineAdapter(activity, null), TweetProvider.TWEET_HOME_TIMELINE_URI, activity));

	    return homeFragment;
	case MENTIONS_TIMELINE:
	    TimelineFragment mentionsFragment = new TimelineFragment();
	    mentionsFragment.setArguments(settingsManager, new MentionsTimelineTweetRequester(), new TweetStorer(
		    activity.getContentResolver(), activity.getSupportLoaderManager(), new TimelineAdapter(activity,
			    null), TweetProvider.TWEET_MENTIONS_TIMELINE_URI, activity));

	    return mentionsFragment;
	case DIRECTS_TIMELINE:
	    TimelineFragment dmsFragment = new TimelineFragment();
	    dmsFragment.setArguments(settingsManager, new DMsTimelineTweetRequester(),
		    new TweetStorer(activity.getContentResolver(), activity.getSupportLoaderManager(),
			    new TimelineAdapter(activity, null), TweetProvider.TWEET_DMS_TIMELINE_URI, activity));
	    return dmsFragment;
	}
	return null;
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public int getCount() {
	return PAGE_COUNT;
    }
}