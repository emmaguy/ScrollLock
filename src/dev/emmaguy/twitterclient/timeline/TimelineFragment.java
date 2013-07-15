package dev.emmaguy.twitterclient.timeline;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import dev.emmaguy.twitterclient.IContainSettings;
import dev.emmaguy.twitterclient.R;
import dev.emmaguy.twitterclient.SettingsManager;
import dev.emmaguy.twitterclient.db.IManageTweetStorage;
import dev.emmaguy.twitterclient.db.TweetProvider;
import dev.emmaguy.twitterclient.ui.ViewHolder;

public class TimelineFragment extends SherlockFragment implements OnItemClickListener, OnItemLongClickListener {
    private ListView listView;
    private OnUserActionListener listener;

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	inflater.inflate(R.menu.menu_timeline, menu);
	super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.delete_button:
	    Toast.makeText(getActivity(), "Deleting...", Toast.LENGTH_SHORT).show();
	    new SettingsManager(getActivity()).clearUserData();
	    // getActivity().getContentResolver().delete(uri, "", null);
	    return true;
	case R.id.refresh_button:
	    Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT).show();
	    new RequestAndStoreNewTweetsAsyncTask(settings, tweetStorer, tweetRequester, settings.getTweetMaxId(),
		    settings.getTweetSinceId(), -1, 1, 1, false).execute();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    @Override
    public void onAttach(Activity activity) {
	super.onAttach(activity);

	try {
	    listener = (OnUserActionListener) activity;
	} catch (ClassCastException e) {
	    throw new ClassCastException(activity.toString() + " must implement OnUserActionListener");
	}
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View arg1, int i, long arg3) {
	Cursor c = ((Cursor) adapterView.getAdapter().getItem(i));
	final String tweetText = c.getString(c.getColumnIndex(TweetProvider.TWEET_TEXT));
	final long tweetUserId = c.getLong(c.getColumnIndex(TweetProvider.USER_USER_ID));

	listener.onOpenTweetDetails(tweetText, tweetUserId);
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

    public interface OnUserActionListener {
	void onOpenTweetDetails(String tweetText, long userId);
    }
}