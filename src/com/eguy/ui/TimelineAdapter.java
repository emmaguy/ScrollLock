package com.eguy.ui;

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
import com.eguy.R;
import com.eguy.db.TweetProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimelineAdapter extends CursorAdapter
{
	private LayoutInflater inflater;

	public TimelineAdapter(TimelineActivity context, Cursor tweetsCursor)
	{
		super(context, tweetsCursor, true);

		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup)
	{
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
	public void bindView(View view, Context context, Cursor cursor)
	{
		ViewHolder holder = (ViewHolder) view.getTag();
		
		String mainUserProfileImg = TweetProvider.TWEET_PROFILE_PIC_URL;
		String mainUserTweeterId = TweetProvider.TWEET_USER_ID;
		
		String username = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_USERNAME));
		byte[] image = cursor.getBlob(cursor.getColumnIndex(TweetProvider.USER_PROFILE_PIC));
		
		int retweetCount = cursor.getInt(cursor.getColumnIndex(TweetProvider.TWEET_RETWEET_COUNT));
		if(retweetCount ==  0)
		{
			holder.RetweetInfo.setVisibility(View.GONE);
			holder.ProfilePictureSmall.setVisibility(View.GONE);
		}
		else
		{
			holder.RetweetInfo.setVisibility(View.VISIBLE);
			
			String rtUsername = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_USERNAME));			
			if(rtUsername != null && !rtUsername.isEmpty())
			{
				holder.ProfilePictureSmall.setVisibility(View.VISIBLE);
				
				int otherRetweetersCount = retweetCount - 1;
				if(otherRetweetersCount > 0)
				{
					holder.RetweetInfo.setText("Retweeted by " + username + " and " + otherRetweetersCount + " other(s)");
				}
				else
				{
					holder.RetweetInfo.setText("Retweeted by " + username);
				}
				
				username = rtUsername;
			}
			else
			{
				holder.ProfilePictureSmall.setVisibility(View.GONE);
				holder.RetweetInfo.setText("Retweets: " + retweetCount);
			}
			
			image = cursor.getBlob(cursor.getColumnIndex(TweetProvider.TWEET_RETWEETED_BY_PROFILE_PIC));
			mainUserProfileImg = TweetProvider.TWEET_RETWEET_PROFILE_PIC_URL;
			mainUserTweeterId = TweetProvider.TWEET_RETWEETED_BY_USER_ID;
		}

		if (image == null || image.length == 0)
		{
			Log.d("ScrollLock", "No user image for user: " + username + " starting async task");

			String url = cursor.getString(cursor.getColumnIndex(mainUserProfileImg));
			long userId = cursor.getLong(cursor.getColumnIndex(mainUserTweeterId));

			new DownloadImageTask(userId, context).execute(url);
		}
		else
		{
			Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
			(holder.ProfilePicture).setImageBitmap(bmp);
		}

		String text = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_TEXT));
		String createdAt = cursor.getString(cursor.getColumnIndex(TweetProvider.TWEET_CREATED_AT));

		holder.TweetText.setText(text);
		holder.Username.setText("@" + username);
		holder.CreatedAt.setText(getFormattedDateTime(createdAt));
	}

	private String getFormattedDateTime(String datetime)
	{
		Date dateTimeOfTweet;
		SimpleDateFormat desiredFormat = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat twitterFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
		try
		{
			dateTimeOfTweet = twitterFormat.parse(datetime);
		}
		catch (ParseException e)
		{
			Log.e("ScrollLock", e.getClass().toString(), e);
			return datetime;
		}

		Calendar tweetDateTime = getTweetDateCalendar(dateTimeOfTweet);

		if (DateMatches(tweetDateTime, getToday()))
			return desiredFormat.format(dateTimeOfTweet);

		if (DateMatches(tweetDateTime, getYesterday()))
			return "Yesterday " + desiredFormat.format(dateTimeOfTweet);

		return formatDateWithTwoLetterContractions(dateTimeOfTweet);
	}

	private String formatDateWithTwoLetterContractions(Date dateTimeOfTweet)
	{
		SimpleDateFormat format = new SimpleDateFormat("d HH:mm");
		String date = format.format(dateTimeOfTweet);

		if (date.endsWith("1") && !date.endsWith("11"))
		{
			format = new SimpleDateFormat("EE MMM d'st' HH:mm");
		}
		else if (date.endsWith("2") && !date.endsWith("12"))
		{
			format = new SimpleDateFormat("EE MMM d'nd' HH:mm");
		}
		else if (date.endsWith("3") && !date.endsWith("13"))
		{
			format = new SimpleDateFormat("EE MMM d'rd' HH:mm");
		}
		else
		{
			format = new SimpleDateFormat("EE MMM d'th' HH:mm");
		}

		return format.format(dateTimeOfTweet);
	}

	private Calendar getTweetDateCalendar(Date dateTimeOfTweet)
	{
		Calendar tweetDateCalendar = Calendar.getInstance();
		tweetDateCalendar.setTime(dateTimeOfTweet);
		return tweetDateCalendar;
	}

	private Calendar getYesterday()
	{
		Calendar yesterday = getToday();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);
		return yesterday;
	}

	private Calendar getToday()
	{
		Calendar today = Calendar.getInstance();
		DateFormat.getDateTimeInstance().setCalendar(today);
		return today;
	}

	private boolean DateMatches(Calendar cal1, Calendar cal2)
	{
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}
}

class ViewHolder
{
	ImageView ProfilePicture;
	ImageView ProfilePictureSmall;
	TextView TweetText;
	TextView Username;
	TextView CreatedAt;
	TextView RetweetInfo;
	boolean IsRetweet;
}
