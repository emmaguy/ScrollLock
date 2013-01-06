package com.eguy.test;

import com.eguy.twitterapi.HomeTimelineUriBuilder;
import android.test.AndroidTestCase;

public class HomeTimelineUriBuilderTest extends AndroidTestCase 
{
	public void testWhen_building_home_timeline_uri_to_request_block_of_tweets()
	{
		String uri = new HomeTimelineUriBuilder("emmaguy", 5, 100, 95, false).build();
		
		assertEquals(uri, "https://api.twitter.com/1.1/statuses/home_timeline.json?screen_name=emmaguy&count=5&since_id=100&max_id=95");
	}
	
	public void testWhen_building_home_timeline_uri_to_request_latest()
	{
		String uri = new HomeTimelineUriBuilder("emmaguy", 5, 100, 0, true).build();
		
		assertEquals(uri, "https://api.twitter.com/1.1/statuses/home_timeline.json?screen_name=emmaguy&count=5&since_id=100");
	}

    public void teststupid()
    {
        assertSame(false, true);
    }
}
