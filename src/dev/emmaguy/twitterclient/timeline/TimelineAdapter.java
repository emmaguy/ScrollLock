package dev.emmaguy.twitterclient.timeline;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import dev.emmaguy.twitterclient.R;
import dev.emmaguy.twitterclient.db.TweetProvider;
import dev.emmaguy.twitterclient.ui.TweetDateFormatter;
import dev.emmaguy.twitterclient.ui.ViewHolder;

public class TimelineAdapter extends CursorAdapter {
    private LayoutInflater inflater;

    public TimelineAdapter(Context context, Cursor tweetsCursor) {
	super(context, tweetsCursor, true);

	this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
	View view = inflater.inflate(R.layout.timeline_row, viewGroup, false);

	ViewHolder holder = new ViewHolder();
	holder.RetweetInfo = ((TextView) view.findViewById(R.id.retweet_information_textview));
	holder.TweetText = ((TextView) view.findViewById(R.id.tweet_textview));
	holder.Username = ((TextView) view.findViewById(R.id.username_textview));
	holder.CreatedAt = ((TextView) view.findViewById(R.id.timestamp_textview));
	holder.ProfilePicture = ((ImageView) view.findViewById(R.id.main_avatar_imageview));
	holder.ProfilePictureSmall = ((ImageView) view.findViewById(R.id.small_avatar_imageview));

	view.setTag(holder);

	return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
	ViewHolder holder = (ViewHolder) view.getTag();

	int retweetCount = cursor.getInt(cursor.getColumnIndex(TweetProvider.TWEET_RETWEET_COUNT));
	String retweetedByUsername = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_USERNAME));
	
	String tweetText = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_TEXT));
	String tweetCreatedAt = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_CREATED_AT));
	String tweetUserUsername = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_USERNAME));
	String tweetUserProfileUrl = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_PROFILE_PIC_URL));	
	long tweetUserUserId = cursor.getLong(cursor.getColumnIndex(TweetProvider.TWEET_USER_ID));
	
	String retweetedByUserUrl = cursor
		.getString(cursor.getColumnIndex(TweetProvider.TWEET_RETWEET_PROFILE_PIC_URL));
	
	if (retweetCount == 0) {
	    constructTweetWithNoRetweetNumber(context, holder, tweetText, tweetCreatedAt, tweetUserUsername, tweetUserUserId, tweetUserProfileUrl);
	} else {
	    if (retweetedByUsername == null || retweetedByUsername.length() <= 0) {
		constructTweetWithRetweetNumber(context, holder, tweetText, tweetCreatedAt, retweetCount, tweetUserUsername, tweetUserUserId, tweetUserProfileUrl);
	    } else {

		long retweetedByUserId = cursor.getLong(cursor.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_USER_ID));
		
		constructTweetWithRetweetNumberAndUsername(context, context.getContentResolver(), holder, tweetText, tweetCreatedAt, retweetedByUsername, retweetedByUserId, retweetedByUserUrl, retweetCount, tweetUserUsername, tweetUserUserId, tweetUserProfileUrl);
	    }
	}
    }
    
    private void setOrRetrieveProfilePicture(Context c, String url, ImageView imageViewToUpdate) {
	Picasso.with(c).load(url).into(imageViewToUpdate);
    }

    private void setTweet(ViewHolder holder, String tweetText, String username, String tweetCreatedAt) {
	holder.TweetText.setText(tweetText);
	holder.Username.setText("@" + username);
	holder.CreatedAt.setText(new TweetDateFormatter().getFormattedDateTime(tweetCreatedAt));
    }

    private void constructTweetWithRetweetNumberAndUsername(Context c, ContentResolver contentResolver, ViewHolder holder, String tweetText, String tweetCreatedAt, String retweetedByUsername, long retweetedByUserId, String retweetedByUserUrl, int retweetCount, String tweetUserUsername, long tweetUserUserId, String tweetUserProfileUrl) {
	holder.RetweetInfo.setVisibility(View.VISIBLE);
	holder.ProfilePictureSmall.setVisibility(View.VISIBLE);

	setTweet(holder, tweetText, retweetedByUsername, tweetCreatedAt);
	setOrRetrieveProfilePicture(c, retweetedByUserUrl, holder.ProfilePicture);
	setOrRetrieveProfilePicture(c, tweetUserProfileUrl, holder.ProfilePictureSmall);

	int otherRetweetersCount = retweetCount - 1;
	if (otherRetweetersCount > 0) {
	    holder.RetweetInfo.setText("Retweeted by " + tweetUserUsername + " and " + otherRetweetersCount + " other(s)");
	} else {
	    holder.RetweetInfo.setText("Retweeted by " + tweetUserUsername);
	}
    }

    private void constructTweetWithRetweetNumber(Context c, ViewHolder holder, String tweetText, String tweetCreatedAt, int retweetCount, String tweetUserUsername, long tweetUserUserId, String tweetUserProfileUrl) {
	holder.ProfilePictureSmall.setVisibility(View.GONE);
	holder.RetweetInfo.setVisibility(View.VISIBLE);
	holder.RetweetInfo.setText("Retweets: " + retweetCount);

	setTweet(holder, tweetText, tweetUserUsername,  tweetCreatedAt);
	setOrRetrieveProfilePicture(c, tweetUserProfileUrl, holder.ProfilePicture);
    }

    private void constructTweetWithNoRetweetNumber(Context c, ViewHolder holder, String tweetText, String tweetCreatedAt, String tweetUserUsername, long tweetUserUserId, String tweetUserProfileUrl) {
	holder.RetweetInfo.setVisibility(View.GONE);
	holder.ProfilePictureSmall.setVisibility(View.GONE);

	setTweet(holder, tweetText, tweetUserUsername, tweetCreatedAt);
	setOrRetrieveProfilePicture(c, tweetUserProfileUrl, holder.ProfilePicture);
    }
}