package dev.emmaguy.twitterclient.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import dev.emmaguy.twitterclient.R;
import dev.emmaguy.twitterclient.SettingsManager;
import dev.emmaguy.twitterclient.db.TweetProvider;
import dev.emmaguy.twitterclient.db.TweetStorer;
import dev.emmaguy.twitterclient.twitterapi.RequestTweetsAndUpdateDbTask;
import dev.emmaguy.twitterclient.twitterapi.TweetRequester;
import dev.emmaguy.twitterclient.ui.SignInFragment.SignInCompleteListener;

public class TimelineFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnClickListener {
    private CursorAdapter adapter;
    private TweetStorer database;

    private ListView listView;
    private boolean isInitialLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	final View v = inflater.inflate(R.layout.fragment_timeline, null);

	listView = ((ListView) v.findViewById(R.id.timeline_listview));

	getActivity().getSupportLoaderManager().initLoader(0, null, this);
	database = new TweetStorer(getActivity().getContentResolver());
	adapter = new TimelineAdapter(getActivity(), null);
	listView.setAdapter(adapter);

	v.findViewById(R.id.refresh_button).setOnClickListener(this);

	// initialiseStoringTweetPositionOnScroll();
	// initialiseRefreshBar();
	// initialiseLongClickToShare();
	initialiseShortClickToOpenTweetViewer();

	return v;
    }

    private void initialiseShortClickToOpenTweetViewer() {
	final Context context = getActivity();

	listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    @Override
	    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		Cursor c = ((Cursor) adapterView.getAdapter().getItem(i));

		Intent intent = new Intent(context, TweetDetailsFragment.class);
		intent.putExtra(TweetProvider.TWEET_TEXT, c.getString(c.getColumnIndex(TweetProvider.TWEET_TEXT)));
		intent.putExtra(TweetProvider.USER_USER_ID, c.getString(c.getColumnIndex(TweetProvider.USER_USER_ID)));
		startActivity(intent);
	    }
	});
    }

//    private void initialiseLongClickToShare() {
//	final Context context = this;
//
//	listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//	    @Override
//	    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//		Toast.makeText(context, "share", Toast.LENGTH_SHORT).show();
//		ViewHolder h = (ViewHolder) view.getTag();
//
//		Intent sendIntent = new Intent();
//		sendIntent.setAction(Intent.ACTION_SEND);
//		sendIntent.putExtra(Intent.EXTRA_TEXT, h.TweetText.getText());
//		sendIntent.setType("text/plain");
//
//		context.startActivity(Intent.createChooser(sendIntent, "Share"));
//		return true;
//	    }
//	});
//
//    }

//    private void initialiseRefreshBar() {
//
//	TextView deleteBar = (TextView) findViewById(R.id.deleteBar);
//	deleteBar.setOnClickListener(new View.OnClickListener() {
//	    @Override
//	    public void onClick(View view) {
//		Toast.makeText(getApplicationContext(), "deleting...", Toast.LENGTH_SHORT).show();
//		getApplicationContext().getContentResolver().delete(TweetProvider.TWEET_URI, "", null);
//	    }
//	});
//    }

    private void getLatestTweets() {
	SettingsManager settingsManager = new SettingsManager(getActivity());

	new RequestTweetsAndUpdateDbTask(settingsManager, database, new TweetRequester(settingsManager.getUsername(),
		settingsManager.getTweetSinceId(), 0, 200, true, settingsManager)).execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
	String[] projection = { TweetProvider.TWEET_TEXT };
	return new CursorLoader(getActivity(), TweetProvider.TWEET_URI, projection, null, null, null);
    }

//    public void initialiseStoringTweetPositionOnScroll() {
//	listView.setOnScrollListener(new OnScrollListener() {
//	    @Override
//	    public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//	    }
//
//	    @Override
//	    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//		int xPos = view.getScrollX();
//		int yPos = view.getScrollY();
//
//		view.scrollTo(xPos, yPos);
//
//		if (!isInitialLoad)
//		    storeScrollPosition();
//	    }
//	});
//
//    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
	adapter.changeCursor(cursor);

//	scrollToLastReadTweetPosition();

	isInitialLoad = false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
	adapter.changeCursor(null);
    }

    // @Override
    // protected void onPause() {
    // super.onPause();
    //
    // //storeScrollPosition();
    // }

//    private void storeScrollPosition() {
//	int indexFromTop = listView.getFirstVisiblePosition();
//	new SettingsManager(getApplicationContext()).setTweetPosition(indexFromTop);
//    }

    // @Override
    // protected void onResume() {
    // super.onResume();
    // Log.i("x", "onResume");
    //
    // Uri uri = getIntent().getData();
    //
    // if(getIntent().getExtras() != null){
    // for(String key : getIntent().getExtras().keySet()){
    // Log.i("x", getIntent().getExtras().getString(key));
    // }
    // }
    // if(uri != null) {
    // Log.i("x", "uri onResume: " + uri);
    // }
    // //scrollToLastReadTweetPosition();
    // }

//    private void scrollToLastReadTweetPosition() {
//	int indexFromTop = new SettingsManager(getActivity()).getTweetPosition();
//	listView.setSelectionFromTop(indexFromTop, 0);
//    }

    @Override
    public void onClick(View v) {
	if (v.getId() == R.id.refresh_button) {
	    Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT).show();
	    getLatestTweets();
	}
    }
}