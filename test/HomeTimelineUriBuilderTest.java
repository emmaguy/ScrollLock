import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

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
		assertThat(uri, equalTo("https://api.twitter.com/1.1/statuses/home_timeline.json?screen_name=emmaguy&count=5&since_id=100&max_id=95"));
	}
	
    @Test
	public void testWhen_building_home_timeline_uri_to_request_latest()
	{
		String uri = new HomeTimelineUriBuilder("emmaguy", 5, 100, 0, true).build();
		
		assertThat(uri, equalTo("https://api.twitter.com/1.1/statuses/home_timeline.json?screen_name=emmaguy&count=5&since_id=100"));
	}
}
