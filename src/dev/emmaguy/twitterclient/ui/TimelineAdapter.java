package dev.emmaguy.twitterclient.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import dev.emmaguy.twitterclient.R;

import dev.emmaguy.twitterclient.db.TweetProvider;

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
	holder.RetweetInfo = ((TextView) view.findViewById(R.id.retweetInfo));
	holder.TweetText = ((TextView) view.findViewById(R.id.tweetText));
	holder.Username = ((TextView) view.findViewById(R.id.username));
	holder.CreatedAt = ((TextView) view.findViewById(R.id.timestamp));
	holder.ProfilePicture = ((ImageView) view.findViewById(R.id.avatarMain));
	holder.ProfilePictureSmall = ((ImageView) view.findViewById(R.id.avatarSmall));

	view.setTag(holder);

	return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
	ViewHolder holder = (ViewHolder) view.getTag();

	String tweetText = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_TEXT));
	String tweetCreatedAt = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_CREATED_AT));
	String tweetUserUsername = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_USERNAME));
	String tweetUserProfileUrl = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_PROFILE_PIC_URL));
	byte[] tweetUserProfileImage = cursor.getBlob(cursor.getColumnIndex(TweetProvider.USER_PROFILE_PIC));
	int tweetRetweetCount = cursor.getInt(cursor.getColumnIndex(TweetProvider.TWEET_RETWEET_COUNT));
	long tweetUserUserId = cursor.getLong(cursor.getColumnIndex(TweetProvider.TWEET_USER_ID));

	long retweetedByUserId = cursor.getLong(cursor.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_USER_ID));
	byte[] retweetedByImage = cursor.getBlob(cursor.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_PROFILE_PIC));
	String retweetedByUsername = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_USERNAME));
	String retweetedByUserUrl = cursor
		.getString(cursor.getColumnIndex(TweetProvider.TWEET_RETWEET_PROFILE_PIC_URL));

	new ConstructTweetUI(holder, context.getContentResolver(), tweetText, tweetCreatedAt, tweetUserUsername,
		tweetUserProfileUrl, tweetUserProfileImage, tweetRetweetCount, tweetUserUserId, retweetedByImage,
		retweetedByUsername, retweetedByUserId, retweetedByUserUrl).Create();
    }
}
