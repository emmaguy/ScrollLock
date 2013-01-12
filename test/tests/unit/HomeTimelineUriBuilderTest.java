package tests.unit;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.eguy.twitterapi.HomeTimelineUriBuilder;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class HomeTimelineUriBuilderTest 
{
    @Test
    public void testWhen_building_home_timeline_uri_to_request_block_of_tweets()
	{
		String uri = new HomeTimelineUriBuilder("emmaguy", 5, 100, 95, false).build();
		
		assertThat(uri, containsString("https://api.twitter.com/1.1/statuses/home_timeline.json"));
		assertThat(uri, containsString("screen_name=emmaguy"));
		assertThat(uri, containsString("count=5"));
		assertThat(uri, containsString("since_id=100"));
		assertThat(uri, containsString("max_id=95"));
	}
	
    @Test
	public void testWhen_building_home_timeline_uri_to_request_latest()
	{
		String uri = new HomeTimelineUriBuilder("emmaguy", 5, 100, 0, true).build();
		
		assertThat(uri, containsString("https://api.twitter.com/1.1/statuses/home_timeline.json"));
		assertThat(uri, containsString("screen_name=emmaguy"));
		assertThat(uri, containsString("count=5"));
		assertThat(uri, containsString("since_id=100"));
		 
		assertThat(uri, not(containsString("max_id=95")));
	}
}
