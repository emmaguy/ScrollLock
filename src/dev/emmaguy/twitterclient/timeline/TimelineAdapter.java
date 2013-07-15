package dev.emmaguy.twitterclient.timeline;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import dev.emmaguy.twitterclient.R;

import dev.emmaguy.twitterclient.db.TweetProvider;
import dev.emmaguy.twitterclient.ui.DownloadImageTask;
import dev.emmaguy.twitterclient.ui.TweetDateFormatter;
import dev.emmaguy.twitterclient.ui.ViewHolder;

public class TimelineAdapter extends CursorAdapter {
    private LayoutInflater inflater;
    private static final List<Long> recentlyRequestedProfilePics = new ArrayList<Long>();
    
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
	byte[] tweetUserProfileImage = cursor.getBlob(cursor.getColumnIndex(TweetProvider.USER_PROFILE_PIC));	
	long tweetUserUserId = cursor.getLong(cursor.getColumnIndex(TweetProvider.TWEET_USER_ID));
	
	String retweetedByUserUrl = cursor
		.getString(cursor.getColumnIndex(TweetProvider.TWEET_RETWEET_PROFILE_PIC_URL));
	
	if (retweetCount == 0) {
	    constructTweetWithNoRetweetNumber(context.getContentResolver(), holder, tweetText, tweetCreatedAt, tweetUserUsername, tweetUserUserId, tweetUserProfileUrl, tweetUserProfileImage);
	} else {
	    if (retweetedByUsername == null || retweetedByUsername.length() <= 0) {
		constructTweetWithRetweetNumber(context.getContentResolver(), holder, tweetText, tweetCreatedAt, retweetCount, tweetUserUsername, tweetUserUserId, tweetUserProfileUrl, tweetUserProfileImage);
	    } else {

		long retweetedByUserId = cursor.getLong(cursor.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_USER_ID));
		byte[] retweetedByImage = cursor.getBlob(cursor.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_PROFILE_PIC));
		
		constructTweetWithRetweetNumberAndUsername(context.getContentResolver(), holder, tweetText, tweetCreatedAt, retweetedByUsername, retweetedByUserId, retweetedByUserUrl, retweetedByImage, retweetCount, tweetUserUsername, tweetUserUserId, tweetUserProfileUrl, tweetUserProfileImage);
	    }
	}
    }
    
    private void setOrRetrieveProfilePicture(ContentResolver contentResolver, long userId, String url, String username, byte[] image,
	    ImageView imageViewToUpdate) {
	if (image == null || image.length == 0) {

	    if (!recentlyRequestedProfilePics.contains(userId)) {
		recentlyRequestedProfilePics.add(userId);
		Log.d("ScrollLock", "No user image for user: " + username + " starting async task");
		new DownloadImageTask(userId, contentResolver).execute(url);
	    }
	} else {
	    Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
	    imageViewToUpdate.setImageBitmap(bmp);
	}
    }

    private void setTweet(ViewHolder holder, String tweetText, String username, String tweetCreatedAt) {
	holder.TweetText.setText(tweetText);
	holder.Username.setText("@" + username);
	holder.CreatedAt.setText(new TweetDateFormatter().getFormattedDateTime(tweetCreatedAt));
    }

    private void constructTweetWithRetweetNumberAndUsername(ContentResolver contentResolver, ViewHolder holder, String tweetText, String tweetCreatedAt, String retweetedByUsername, long retweetedByUserId, String retweetedByUserUrl, byte[] retweetedByImage, int retweetCount, String tweetUserUsername, long tweetUserUserId, String tweetUserProfileUrl, byte[] tweetUserProfileImage) {
	holder.RetweetInfo.setVisibility(View.VISIBLE);
	holder.ProfilePictureSmall.setVisibility(View.VISIBLE);

	setTweet(holder, tweetText, retweetedByUsername, tweetCreatedAt);
	setOrRetrieveProfilePicture(contentResolver, retweetedByUserId, retweetedByUserUrl, retweetedByUsername, retweetedByImage,
		holder.ProfilePicture);
	setOrRetrieveProfilePicture(contentResolver, tweetUserUserId, tweetUserProfileUrl, tweetUserUsername, tweetUserProfileImage,
		holder.ProfilePictureSmall);

	int otherRetweetersCount = retweetCount - 1;
	if (otherRetweetersCount > 0) {
	    holder.RetweetInfo.setText("Retweeted by " + tweetUserUsername + " and " + otherRetweetersCount + " other(s)");
	} else {
	    holder.RetweetInfo.setText("Retweeted by " + tweetUserUsername);
	}
    }

    private void constructTweetWithRetweetNumber(ContentResolver contentResolver, ViewHolder holder, String tweetText, String tweetCreatedAt, int retweetCount, String tweetUserUsername, long tweetUserUserId, String tweetUserProfileUrl, byte[] tweetUserProfileImage) {
	holder.RetweetInfo.setVisibility(View.VISIBLE);
	holder.RetweetInfo.setText("Retweets: " + retweetCount);

	holder.ProfilePictureSmall.setVisibility(View.GONE);

	setTweet(holder, tweetText, tweetUserUsername,  tweetCreatedAt);
	setOrRetrieveProfilePicture(contentResolver, tweetUserUserId, tweetUserProfileUrl, tweetUserUsername, tweetUserProfileImage, holder.ProfilePicture);
    }

    private void constructTweetWithNoRetweetNumber(ContentResolver contentResolver, ViewHolder holder, String tweetText, String tweetCreatedAt, String tweetUserUsername, long tweetUserUserId, String tweetUserProfileUrl, byte[] tweetUserProfileImage) {
	holder.RetweetInfo.setVisibility(View.GONE);
	holder.ProfilePictureSmall.setVisibility(View.GONE);

	setTweet(holder, tweetText, tweetUserUsername, tweetCreatedAt);
	setOrRetrieveProfilePicture(contentResolver, tweetUserUserId, tweetUserProfileUrl, tweetUserUsername, tweetUserProfileImage, holder.ProfilePicture);
    }
}