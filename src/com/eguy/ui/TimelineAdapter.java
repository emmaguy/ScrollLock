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
import com.eguy.db.TweetDatabase;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimelineAdapter extends CursorAdapter
{
    private LayoutInflater inflater;
    private TweetDatabase tweetDatabase;

    public TimelineAdapter(TimelineActivity context, Cursor tweetsCursor, TweetDatabase tweetDatabase)
    {
        super(context, tweetsCursor, true);

        this.tweetDatabase = tweetDatabase;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup)
    {
        return inflater.inflate(R.layout.timeline_row, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        byte[] image = cursor.getBlob(cursor.getColumnIndex(TweetDatabase.USER_PROFILE_PIC));
        if (image == null || image.length == 0)
        {
            String url = cursor.getString(cursor.getColumnIndex(TweetDatabase.TWEET_PROFILE_PIC_URL));
            long userId = cursor.getLong(cursor.getColumnIndex(TweetDatabase.TWEET_USER_ID));
            new DownloadImageTask(((ImageView) view.findViewById(R.id.avatar)), userId, tweetDatabase, this).execute(url);
        }
        else
        {
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            ((ImageView) view.findViewById(R.id.avatar)).setImageBitmap(bmp);
        }

        String text = cursor.getString(cursor.getColumnIndex(TweetDatabase.TWEET_TEXT));
        String createdAt = cursor.getString(cursor.getColumnIndex(TweetDatabase.TWEET_CREATED_AT));
        String username = cursor.getString(cursor.getColumnIndex(TweetDatabase.TWEET_USERNAME));

        ((TextView) view.findViewById(R.id.tweetText)).setText(text);
        ((TextView) view.findViewById(R.id.username)).setText(username);
        ((TextView) view.findViewById(R.id.timestamp)).setText(getFormattedDateTime(createdAt));
    }

    private String getFormattedDateTime(String datetime)
    {
        String formattedDateTime = datetime;
        SimpleDateFormat desiredFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat twitterFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        try
        {
            Date dateObj = twitterFormat.parse(datetime);
            formattedDateTime = desiredFormat.format(dateObj);
        } catch (ParseException e)
        {
            Log.e("ScrollLock", e.getClass().toString(), e);
        }
        return formattedDateTime;
    }
}
