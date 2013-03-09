package com.eguy.ui;

import oauth.signpost.OAuthConsumer;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eguy.R;
import com.eguy.SettingsManager;
import com.eguy.db.TweetProvider;
import com.eguy.db.TweetStorer;
import com.eguy.oauth.AuthenticateActivity;
import com.eguy.oauth.OAuthProviderAndConsumer;
import com.eguy.twitterapi.FakeTweetInserterTask;
import com.eguy.twitterapi.RequestTweetsAndUpdateDbTask;
import com.eguy.twitterapi.TweetRequester;

public class TimelineActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>
{
	private CursorAdapter adapter;
	private TweetStorer database;
	
	private ListView listView;
	private boolean isInitialLoad = true;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.timeline_listview);
		
		listView = ((ListView) findViewById(R.id.lstTimeline));

		getLoaderManager().initLoader(0, null, this);
		database = new TweetStorer(this.getApplicationContext().getContentResolver());
		adapter = new TimelineAdapter(this, null);
		listView.setAdapter(adapter);

		getLatestTweetsOrAuthenticate();
		
		initialiseStoringTweetPositionOnScroll();
		initialiseRefreshBar();
		initialiseLongClickToShare();
		initialiseShortClickToOpenTweetViewer();
	}

	private void initialiseShortClickToOpenTweetViewer()
	{
		final Context context = this;

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				Cursor c = ((Cursor) adapterView.getAdapter().getItem(i));

				Intent intent = new Intent(context, TweetViewerActivity.class);
				intent.putExtra(TweetProvider.TWEET_TEXT, c.getString(c.getColumnIndex(TweetProvider.TWEET_TEXT)));
				intent.putExtra(TweetProvider.USER_USER_ID, c.getString(c.getColumnIndex(TweetProvider.USER_USER_ID)));
				startActivity(intent);
			}
		});
	}

	private void initialiseLongClickToShare()
	{
		final Context context = this;

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				Toast.makeText(context, "share", Toast.LENGTH_SHORT).show();
				ViewHolder h = (ViewHolder) view.getTag();

				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, h.TweetText.getText());
				sendIntent.setType("text/plain");

				context.startActivity(Intent.createChooser(sendIntent, "Share"));
				return true;
			}
		});

	}

	private void getLatestTweetsOrAuthenticate()
	{
		SettingsManager settingsManager = new SettingsManager(this.getApplicationContext());
		if (settingsManager.credentialsAvailable())
		{
			getLatestTweets();
		}
		else
		{
			startActivity(new Intent(this, AuthenticateActivity.class));
		}
	}

	private void initialiseRefreshBar()
	{
		TextView refreshBar = (TextView) findViewById(R.id.refreshBar);
		refreshBar.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Toast.makeText(getApplicationContext(), "refreshing...", Toast.LENGTH_SHORT).show();
				getLatestTweets();
				//new FakeTweetInserterTask(getApplicationContext()).execute();
			}
		});
	}

	private void getLatestTweets()
	{
		SettingsManager settingsManager = new SettingsManager(this.getApplicationContext());
		OAuthConsumer consumer = new OAuthProviderAndConsumer(settingsManager).getConsumer();

		Log.d("ScrollLock", "starting run");
		Log.d("ScrollLock", "since_id: " + settingsManager.getTweetSinceId());
		Log.d("ScrollLock", "max_id: " + settingsManager.getTweetMaxId());

		new RequestTweetsAndUpdateDbTask(settingsManager, database, new TweetRequester(consumer,
				settingsManager.getUsername(), settingsManager.getTweetSinceId(), 0, 200, true)).execute();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
	{
		String[] projection = { TweetProvider.TWEET_TEXT };
		return new CursorLoader(this, TweetProvider.TWEET_URI, projection, null, null, null);
	}

    public void initialiseStoringTweetPositionOnScroll() 
	{
        listView.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
					
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				int xPos = view.getScrollX();
				int yPos = view.getScrollY();
				
				view.scrollTo(xPos, yPos);
				
				if(!isInitialLoad)
					storeScrollPosition();
			}
		});
        
    }
	
	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
	{
		adapter.changeCursor(cursor);
		
		scrollToLastReadTweetPosition();
		
		isInitialLoad = false;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader)
	{
		adapter.changeCursor(null);
	}
	
	@Override
	protected void onPause() 
	{
	    super.onPause();
	    
	    storeScrollPosition();
	}

	private void storeScrollPosition()
	{
		int indexFromTop = listView.getFirstVisiblePosition();
	    new SettingsManager(getApplicationContext()).setTweetPosition(indexFromTop);
	}
	
	@Override
	protected void onResume() 
	{
	    super.onResume();
	    
	    scrollToLastReadTweetPosition();
	}

	private void scrollToLastReadTweetPosition()
	{
		int indexFromTop = new SettingsManager(getApplicationContext()).getTweetPosition();
	    listView.setSelectionFromTop(indexFromTop, 0);
	}
}