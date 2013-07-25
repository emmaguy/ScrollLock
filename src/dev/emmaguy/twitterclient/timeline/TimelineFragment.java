package dev.emmaguy.twitterclient.timeline;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import dev.emmaguy.twitterclient.IContainSettings;
import dev.emmaguy.twitterclient.R;
import dev.emmaguy.twitterclient.SettingsManager;
import dev.emmaguy.twitterclient.db.IManageTweetStorage;
import dev.emmaguy.twitterclient.db.TweetProvider;
import dev.emmaguy.twitterclient.db.TweetStorer;
import dev.emmaguy.twitterclient.timeline.details.TweetDetailsFragment;
import dev.emmaguy.twitterclient.ui.IContainPullToRefreshAttacher;
import dev.emmaguy.twitterclient.ui.ViewHolder;

public class TimelineFragment extends SherlockFragment implements OnItemClickListener, OnItemLongClickListener,
	OnRefreshListener, OnRefreshTimelineComplete {
    private ListView listView;
    private IRequestTweets tweetRequester;
    private IManageTweetStorage tweetStorer;
    private PullToRefreshAttacher pullToRefreshAttacher;
    private IContainSettings settingsManager;

    private static final String FRAGMENT_POSITION = "position";
    public static final int HOME_TIMELINE = 0;
    public static final int MENTIONS_TIMELINE = 1;
    public static final int DIRECTS_TIMELINE = 2;

    public static TimelineFragment newInstance(int position) {
	TimelineFragment t = new TimelineFragment();

	Bundle args = new Bundle();
	args.putInt(FRAGMENT_POSITION, position);
	t.setArguments(args);

	return t;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	final View v = inflater.inflate(R.layout.fragment_timeline, null);

	int position = getArguments().getInt(FRAGMENT_POSITION, -1);
	switch (position) {

	case HOME_TIMELINE:
	    this.settingsManager = new SettingsManager(getActivity());
	    this.tweetRequester = new HomeTimelineTweetRequester(settingsManager);
	    this.tweetStorer = new TweetStorer(getActivity().getContentResolver(), getActivity()
		    .getSupportLoaderManager(), new TimelineAdapter(getActivity(), null),
		    TweetProvider.TWEET_HOME_TIMELINE_URI, getActivity());
            break;
	case MENTIONS_TIMELINE:
	    this.settingsManager = new SettingsManager(getActivity());
	    this.tweetRequester = new MentionsTimelineTweetRequester(settingsManager);
	    this.tweetStorer = new TweetStorer(getActivity().getContentResolver(), getActivity()
		    .getSupportLoaderManager(), new TimelineAdapter(getActivity(), null),
		    TweetProvider.TWEET_MENTIONS_TIMELINE_URI, getActivity());
            break;
	case DIRECTS_TIMELINE:
	    this.settingsManager = new SettingsManager(getActivity());
	    this.tweetRequester = new DMsTimelineTweetRequester(settingsManager);
	    this.tweetStorer = new TweetStorer(getActivity().getContentResolver(), getActivity()
		    .getSupportLoaderManager(), new TimelineAdapter(getActivity(), null),
		    TweetProvider.TWEET_DMS_TIMELINE_URI, getActivity());
            break;
	}

	listView = ((ListView) v.findViewById(R.id.timeline_listview));
	listView.setAdapter(tweetStorer.getAdapter());
	listView.setOnItemClickListener(this);
	listView.setOnItemLongClickListener(this);

	return v;
    }

    public ListView getListView() {
	return listView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View arg1, int i, long arg3) {
	Cursor c = ((Cursor) adapterView.getAdapter().getItem(i));

	String avatarUrl = c.getString(c.getColumnIndex(TweetProvider.TWEET_RETWEET_PROFILE_PIC_URL));
	String tweetText = c.getString(c.getColumnIndex(TweetProvider.TWEET_TEXT));
	String retweetedByUsername = c.getString(c.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_USERNAME));
	String tweetUserUsername = c.getString(c.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_USERNAME));
	int retweetCount = c.getInt(c.getColumnIndex(TweetProvider.TWEET_RETWEET_COUNT));

	if (retweetCount <= 0 || retweetedByUsername == null || retweetedByUsername.length() <= 0) {
	    tweetUserUsername = c.getString(c.getColumnIndex(TweetProvider.TWEET_USERNAME));
	    avatarUrl = c.getString(c.getColumnIndex(TweetProvider.TWEET_PROFILE_PIC_URL));
	}

	String tweetCreatedAt = c.getString(c.getColumnIndex(TweetProvider.TWEET_CREATED_AT));

	TweetDetailsFragment tweetDetailsFragment = new TweetDetailsFragment();
	tweetDetailsFragment.setTweet(tweetText, tweetCreatedAt, tweetUserUsername, avatarUrl);

	// create the details fragment as a child so we can go back afterwards
	FragmentTransaction transaction = (FragmentTransaction) getChildFragmentManager().beginTransaction();
	transaction.add(R.id.timeline_fragment_container, tweetDetailsFragment);
	transaction.addToBackStack(null);
	transaction.commit();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
	Toast.makeText(getActivity(), "Sharing...", Toast.LENGTH_SHORT).show();
	ViewHolder h = (ViewHolder) view.getTag();

	Intent sendIntent = new Intent();
	sendIntent.setAction(Intent.ACTION_SEND);
	sendIntent.putExtra(Intent.EXTRA_TEXT, h.TweetText.getText());
	sendIntent.setType("text/plain");

	getActivity().startActivity(Intent.createChooser(sendIntent, "Share"));
	return true;
    }

    public boolean isShowingTweetDetailsFragment() {
	Fragment f = getChildFragmentManager().findFragmentById(R.id.timeline_fragment_container);
	return f != null;
    }

    @Override
    public void onAttach(Activity activity) {
	super.onAttach(activity);

	try {
	    pullToRefreshAttacher = ((IContainPullToRefreshAttacher) activity).getRefreshAttacher();
	} catch (ClassCastException e) {
	    throw new ClassCastException(activity.toString() + " must implement IContainPullToRefreshAttacher");
	}
    }

    @Override
    public void onRefreshStarted(View arg0) {
	refresh();
    }

    public void refresh() {
	new RequestAndStoreNewTweetsAsyncTask(settingsManager.getUserToken(), settingsManager.getUserTokenSecret(),
		tweetStorer, tweetRequester, tweetRequester.getTweetMaxId(), tweetRequester.getTweetSinceId(), -1,
		tweetRequester.getNumberOfTweetsToRequest(), 1, false, (OnRefreshTimelineComplete) this).execute();
    }

    @Override
    public void refreshComplete() {
	pullToRefreshAttacher.setRefreshComplete();
    }
}