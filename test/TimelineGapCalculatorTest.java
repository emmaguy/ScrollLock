import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.eguy.twitterapi.TimelineAction;
import com.eguy.twitterapi.TimelineGapCalculator;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class TimelineGapCalculatorTest 
{
    @Test
    public void testWhen_calculating_for_a_timeline_which_has_a_gap_latest_tweet_50_processed_20()
	{
    	long oldestTweetId = 50;
    	long oldestProcessedTweet = 20;
    	
    	TimelineAction gapCalculator = new TimelineGapCalculator(oldestTweetId, oldestProcessedTweet).calculate();
    	
    	Assert.assertTrue(gapCalculator.requestMoreTweest());
    	Assert.assertEquals(20, gapCalculator.getTopOfGap());
    	Assert.assertEquals(49, gapCalculator.getBottomOfGap());
	}
    
    @Test
    public void testWhen_calculating_for_a_timeline_which_has_a_gap_latest_tweet_29_processed_20()
	{
    	long oldestTweetId = 29;
    	long oldestProcessedTweet = 20;
    	
    	TimelineAction gapCalculator = new TimelineGapCalculator(oldestTweetId, oldestProcessedTweet).calculate();
    	
    	Assert.assertTrue(gapCalculator.requestMoreTweest());
    	Assert.assertEquals(20, gapCalculator.getTopOfGap());
    	Assert.assertEquals(28, gapCalculator.getBottomOfGap());
	}
    
    @Test
    public void testWhen_calculating_for_a_timeline_which_no_longer_has_a_gap()
	{
    	long oldestTweetId = 20;
    	long oldestProcessedTweet = 20;
    	
    	TimelineAction gapCalculator = new TimelineGapCalculator(oldestTweetId, oldestProcessedTweet).calculate();
    	
    	Assert.assertFalse(gapCalculator.requestMoreTweest());
	}
}