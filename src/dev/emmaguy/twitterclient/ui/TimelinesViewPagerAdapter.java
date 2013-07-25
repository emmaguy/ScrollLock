package dev.emmaguy.twitterclient.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import dev.emmaguy.twitterclient.timeline.TimelineFragment;

public class TimelinesViewPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 3;
    
    private final SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public TimelinesViewPagerAdapter(final FragmentManager fm) {
	super(fm);
    }

    @Override
    public Fragment getItem(int position) {
	return TimelineFragment.newInstance(position);
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