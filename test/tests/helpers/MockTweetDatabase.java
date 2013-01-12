package tests.helpers;
import android.content.ContentValues;

import com.eguy.db.IStoreTweets;

public class MockTweetDatabase implements IStoreTweets
{
	private int numberOfTweetsSaved = 0;
	
	public int getNumberOfTweetsSaved()
	{
		return numberOfTweetsSaved;
	}
	
	@Override
	public void addTweets(ContentValues[] tweets)
	{
		numberOfTweetsSaved += tweets.length;
	}
}
