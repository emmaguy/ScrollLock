package com.eguy.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import com.eguy.db.TweetDatabase;

import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
{
    private ImageView imageView;
    private long userId;
    private TweetDatabase tweetDatabase;
    private CursorAdapter cursorAdapter;

    public DownloadImageTask(ImageView view, long userId, TweetDatabase tweetDatabase, CursorAdapter cursorAdapter)
    {
        this.imageView = view;
        this.userId = userId;
        this.tweetDatabase = tweetDatabase;
        this.cursorAdapter = cursorAdapter;
    }

    protected Bitmap doInBackground(String... urls)
    {
        String imageUrl = urls[0];
        Bitmap bitmap = null;
        try
        {
            InputStream in = new URL(imageUrl).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e)
        {
            Log.e("ScrollLock", e.getClass().toString(), e);
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result)
    {
        imageView.setImageBitmap(result);
        tweetDatabase.addUserProfilePicture(result, userId);
        cursorAdapter.notifyDataSetChanged();
    }
}