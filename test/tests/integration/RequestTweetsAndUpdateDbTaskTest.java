package tests.integration;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import tests.helpers.MockSettingsManager;
import tests.helpers.MockTweetDatabase;
import tests.helpers.MockTweetRequester;
import android.util.Log;

import com.eguy.twitterapi.RequestTweetsAndUpdateDbTask;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.internal.Implements;

@RunWith(RobolectricTestRunner.class)
public class RequestTweetsAndUpdateDbTaskTest
{
	@Test
	public void testWhen_requesting_latest_tweets_and_timeline_has_a_gap() throws InterruptedException
	{
		Robolectric.bindShadowClass(ShadowLog.class);

		// request latest 5 tweets, newer than id 100
		MockTweetRequester requestTweets = new MockTweetRequester(5, 100, 0);
		MockTweetDatabase database = new MockTweetDatabase();
		MockSettingsManager settings = new MockSettingsManager();

		settings.setTweetSinceId(100);
		settings.setTweetMaxId(10);

		new RequestTweetsAndUpdateDbTask(settings, database, requestTweets).execute();

		Thread.sleep(30000);

		Assert.assertEquals(90, database.getNumberOfTweetsSaved());
	}

	@Implements(Log.class)
	public static class ShadowLog
	{
		public static int d(java.lang.String tag, java.lang.String msg)
		{
			System.out.println("[" + tag + "] " + msg);
			return 0;
		}
	}
}
