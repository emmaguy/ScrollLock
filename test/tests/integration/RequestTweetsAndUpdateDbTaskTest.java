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

		// request latest tweets - sinceId much higher than maxId
		// and let the async task recurse to fill in the gap
		MockTweetRequester requestTweets = new MockTweetRequester(5, 100, 0);
		MockTweetDatabase database = new MockTweetDatabase();
		MockSettingsManager settings = new MockSettingsManager();

		settings.setTweetSinceId(100);
		settings.setTweetMaxId(10);

		new RequestTweetsAndUpdateDbTask(settings, database, requestTweets).execute();

		// latest tweets from 100 to 104 (5 tweets), plus gap (10 to 99, 89
		// tweets)
		Assert.assertEquals(94, database.getNumberOfTweetsSaved());

		Assert.assertEquals(104, database.getMaxTweetId());
		Assert.assertEquals(11, database.getMinTweetId());

		// settings should have been updated after gap has been filled
		Assert.assertEquals(104, settings.getTweetSinceId());
		Assert.assertEquals(104, settings.getTweetMaxId());
	}

	@Implements(Log.class)
	public static class ShadowLog
	{
		public static int d(java.lang.String tag, java.lang.String msg)
		{
			System.out.println("[" + tag + ", debug] " + msg);
			return 0;
		}

		public static int e(java.lang.String tag, java.lang.String msg)
		{
			System.out.println("[" + tag + ", error] " + msg);
			return 0;
		}
	}
}
