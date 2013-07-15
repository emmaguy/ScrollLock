package dev.emmaguy.twitterclient.timeline;

import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.ContentValues;
import dev.emmaguy.twitterclient.db.TweetProvider;

public class DMsTimelineTweetRequester implements IRequestTweets {

    private List<DirectMessage> directMessages;

    @Override
    public void requestTweets(Twitter twitter, int pageId, int numberOfTweetsToRequest, long sinceId, long maxId)
	    throws TwitterException {
	directMessages = twitter.getDirectMessages();
    }

    @Override
    public ContentValues[] getTweets() {
	final int size = directMessages.size();
	ContentValues[] values = new ContentValues[size];
	for (int i = 0; i < size; i++) {

	    DirectMessage dm = directMessages.get(i);
	    
	    ContentValues tweetValue = new ContentValues();
	    tweetValue.put(TweetProvider.TWEET_ID, dm.getId());
	    tweetValue.put(TweetProvider.TWEET_TEXT, dm.getText());
	    tweetValue.put(TweetProvider.TWEET_CREATED_AT, dm.getCreatedAt().toString());
	    tweetValue.put(TweetProvider.TWEET_USER_ID, dm.getSender().getId());
	    tweetValue.put(TweetProvider.TWEET_USERNAME, dm.getSender().getScreenName());
	    tweetValue.put(TweetProvider.TWEET_PROFILE_PIC_URL, dm.getSender().getOriginalProfileImageURL());
	    tweetValue.put(TweetProvider.TWEET_IS_DM, true);

	    values[i] = tweetValue;
	}
	return values;
    }

    @Override
    public TimelineUpdate getTimelineUpdate() {
	return new TimelineUpdate().buildFromDMs(directMessages);
    }
}
