package dev.emmaguy.twitterclient.timeline;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
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
import dev.emmaguy.twitterclient.db.IManageTweetStorage;
import dev.emmaguy.twitterclient.db.TweetProvider;
import dev.emmaguy.twitterclient.timeline.details.TweetDetailsFragment;
import dev.emmaguy.twitterclient.ui.ViewHolder;

public class TimelineFragment extends SherlockFragment implements OnItemClickListener, OnItemLongClickListener {
    private ListView listView;
    private IContainSettings settings;
    private IRequestTweets tweetRequester;
    private IManageTweetStorage tweetStorer;

    public void setArguments(IContainSettings settings, IRequestTweets tweetRequester, IManageTweetStorage tweetStorer) {
	this.settings = settings;
	this.tweetRequester = tweetRequester;
	this.tweetStorer = tweetStorer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	final View v = inflater.inflate(R.layout.fragment_timeline, null);

	listView = ((ListView) v.findViewById(R.id.timeline_listview));
	listView.setAdapter(tweetStorer.getAdapter());
	listView.setOnItemClickListener(this);
	listView.setOnItemLongClickListener(this);

	return v;
    }

    public void refresh() {
	Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT).show();
	new RequestAndStoreNewTweetsAsyncTask(settings, tweetStorer, tweetRequester, settings.getTweetMaxId(),
		settings.getTweetSinceId(), -1, settings.getNumberOfTweetsToRequest(), 1, false).execute();
    }
    
    @Override
    public void onItemClick(AdapterView<?> adapterView, View arg1, int i, long arg3) {
	Cursor c = ((Cursor) adapterView.getAdapter().getItem(i));
	final String tweetText = c.getString(c.getColumnIndex(TweetProvider.TWEET_TEXT));

	String retweetedByUsername = c.getString(c.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_USERNAME));
	int retweetCount = c.getInt(c.getColumnIndex(TweetProvider.TWEET_RETWEET_COUNT));

	String tweetUserUsername = c.getString(c.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_USERNAME));
	byte[] tweetUserProfileImage = c.getBlob(c.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_PROFILE_PIC));

	if (retweetCount <= 0 || retweetedByUsername == null || retweetedByUsername.length() <= 0) {
	    tweetUserProfileImage = c.getBlob(c.getColumnIndex(TweetProvider.USER_PROFILE_PIC));
	    tweetUserUsername = c.getString(c.getColumnIndex(TweetProvider.TWEET_USERNAME));
	}

	String tweetCreatedAt = c.getString(c.getColumnIndex(TweetProvider.TWEET_CREATED_AT));

	TweetDetailsFragment tweetDetailsFragment = new TweetDetailsFragment();
	tweetDetailsFragment.setTweet(tweetText,
		BitmapFactory.decodeByteArray(tweetUserProfileImage, 0, tweetUserProfileImage.length), tweetCreatedAt,
		tweetUserUsername);

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
}