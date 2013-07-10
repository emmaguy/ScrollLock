package tests.helpers;
import java.util.HashSet;

import android.content.ContentValues;
import android.util.Log;

import dev.emmaguy.twitterclient.db.IStoreTweets;
import dev.emmaguy.twitterclient.db.TweetProvider;

public class MockTweetDatabase implements IStoreTweets
{
	private HashSet<Long> receivedTweetIds = new HashSet<Long>();
	
	public int getNumberOfTweetsSaved()
	{
		return receivedTweetIds.size();
	}
	
	public long getMinTweetId()
	{
		long minId = Long.MAX_VALUE;
		for(long id : receivedTweetIds)
		{
			if(id < minId)
				minId = id;
		}
		return minId;
	}
	
	public long getMaxTweetId()
	{
		long maxId = 0;
		for(long id : receivedTweetIds)
		{
			if(id > maxId)
				maxId = id;
		}
		return maxId;
	}
	
	@Override
	public void addTweets(ContentValues[] tweets)
	{
		for(ContentValues v : tweets)
		{
			long id = (Long) v.get(TweetProvider.TWEET_ID);
			Log.d("ScrollLock", "saving tweet: " + id);
			receivedTweetIds.add(id);
		}
	}
}
