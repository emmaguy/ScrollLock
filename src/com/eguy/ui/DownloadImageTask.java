package com.eguy.ui;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.eguy.db.TweetProvider;

public class DownloadImageTask extends AsyncTask<String, Void, byte[]>
{;
	private long userId;
	private Context context;

	public DownloadImageTask(long userId, Context context)
	{
		this.userId = userId;
		this.context = context;
	}

	protected byte[] doInBackground(String... urls)
	{
		String imageUrl = urls[0];

		try
		{
			InputStream in = new URL(imageUrl).openStream();
			BufferedInputStream bis = new BufferedInputStream(in);

			ByteArrayBuffer baf = new ByteArrayBuffer(500);
			int current = 0;
			while ((current = bis.read()) != -1)
			{
				baf.append((byte) current);
			}

			return baf.toByteArray();
		}
		catch (Exception e)
		{
			Log.e("ScrollLock", e.getClass().toString(), e);
		}
		return null;
	}

	protected void onPostExecute(byte[] bytes)
	{
		ContentValues values = new ContentValues();
		values.put(TweetProvider.USER_USER_ID, userId);
		values.put(TweetProvider.USER_PROFILE_PIC, bytes);
		context.getContentResolver().insert(TweetProvider.USER_URI, values);
	}
}