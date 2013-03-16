package com.eguy.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class TweetProvider extends ContentProvider
{
	private TweetDatabase tweetDatabase;

	public static final int TWEET_TIMELINE_QUERY = 1;
	public static final int USER_QUERY = 2;
	public static final int INVALID_URI = -1;
	public static final String AUTHORITY = "com.eguy.db.TweetProvider";

	public static final String TWEET_TABLE_NAME = "Tweet";

	public static final String TWEET_ID = BaseColumns._ID;
	public static final String TWEET_USER_ID = "UserId";

	public static final String TWEET_TEXT = "Text";
	public static final String TWEET_CREATED_AT = "CreatedAt";
	public static final String TWEET_USERNAME = "Username";
	public static final String TWEET_PROFILE_PIC_URL = "ProfilePictureUrl";
	
	public static final String TWEET_RETWEET_COUNT = "RetweetCount";
	public static final String TWEET_RETWEET_PROFILE_PIC_URL = "RtProfilePictureUrl";
	public static final String TWEET_RETWEETED_BY_USER_ID = "RetweetUserId";
	public static final String TWEET_RETWEETED_BY_USERNAME = "RetweetUsername";
	public static final String TWEET_RETWEETED_BY_PROFILE_PIC = "RtProfilePic";
	
	public static final String USER_TABLE_NAME = "User";
	public static final String USER_USER_ID = "UserId";
	public static final String USER_PROFILE_PIC = "ProfilePicture";

	private static final UriMatcher uriMatcher;

	static
	{
		uriMatcher = new UriMatcher(0);
		uriMatcher.addURI(AUTHORITY, TWEET_TABLE_NAME, TWEET_TIMELINE_QUERY);
		uriMatcher.addURI(AUTHORITY, USER_TABLE_NAME, USER_QUERY);
	}

	public static final String SCHEME = "content";
	public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY);

	public static Uri TWEET_URI = Uri.withAppendedPath(CONTENT_URI, TWEET_TABLE_NAME);
	public static Uri USER_URI = Uri.withAppendedPath(CONTENT_URI, USER_TABLE_NAME);

	@Override
	public boolean onCreate()
	{
		tweetDatabase = new TweetDatabase(getContext());
		
//		SQLiteDatabase db = tweetDatabase.getWritableDatabase();
//		db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
//		db.execSQL("CREATE TABLE " + USER_TABLE_NAME + " (" + USER_USER_ID + "  BIGINT PRIMARY KEY NOT NULL, "
//				+ USER_PROFILE_PIC + " BLOB NOT NULL" + ");");
//		
		// db.execSQL("DELETE FROM " + TWEET_TABLE_NAME + " WHERE " + TWEET_TEXT
		// + " LIKE 'Generated Tweet:%'");

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2)
	{
		return getTweetsCursor(uri);
	}

	public Cursor getTweetsCursor(Uri uri)
	{
		Cursor cur = null;

		try
		{
			SQLiteDatabase database = tweetDatabase.getReadableDatabase();
			cur = database.rawQuery(
			
			"SELECT t." + 
						TWEET_USER_ID + "," + 
						TWEET_ID + "," + 
						TWEET_TEXT + "," + 
						TWEET_CREATED_AT + "," + 
						TWEET_USERNAME + "," + 
						TWEET_PROFILE_PIC_URL + "," + 
						" u." + USER_PROFILE_PIC +"," +
						TWEET_RETWEET_COUNT + "," +
						TWEET_RETWEET_PROFILE_PIC_URL + "," +
						TWEET_RETWEETED_BY_USER_ID + "," +
						TWEET_RETWEETED_BY_USERNAME  +"," +
						" u1." + USER_PROFILE_PIC + " AS " + TWEET_RETWEETED_BY_PROFILE_PIC + 
			" FROM " + TWEET_TABLE_NAME + " t " + 
						" LEFT JOIN " + USER_TABLE_NAME + " u ON t." + TWEET_USER_ID + " = u." + USER_USER_ID +
						" LEFT JOIN " + USER_TABLE_NAME + " u1 ON t." + TWEET_RETWEETED_BY_USER_ID + " = u1." + USER_USER_ID +
			" ORDER BY t." + TWEET_ID + " ASC ", null);
			
			cur.setNotificationUri(getContext().getContentResolver(), uri);
		}
		catch (Exception ex)
		{
			Log.d("db", ex.toString());
		}
		return cur;
	}

	@Override
	public String getType(Uri uri)
	{
		return TWEET_TABLE_NAME;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues)
	{
		switch (uriMatcher.match(uri))
		{
		case USER_QUERY:
		{
			long id = -1;

			try
			{
				SQLiteDatabase writableDatabase = tweetDatabase.getWritableDatabase();
				id = writableDatabase.insert(USER_TABLE_NAME, null, contentValues);
			}
			catch (Exception e)
			{
				Log.d("ScrollLockDb", e.getClass().toString(), e);
			}

			if (-1 != id)
			{
				getContext().getContentResolver().notifyChange(TweetProvider.TWEET_URI, null);
				return Uri.withAppendedPath(uri, Long.toString(id));
			}
			else
			{
				Log.d("ScrollLockDb", "Insert error for userid/profile picture");
				return uri;
			}
		}
		case TWEET_TIMELINE_QUERY:
		{
			long insertedId = -1;
			try
			{
				SQLiteDatabase sqldb = tweetDatabase.getWritableDatabase();
				insertedId = sqldb.insert(TWEET_TABLE_NAME, null, contentValues);
			}
			catch (Exception e)
			{
				Log.d("ScrollLockDb", e.getClass().toString(), e);
			}

			if (-1 != insertedId)
			{
				getContext().getContentResolver().notifyChange(TweetProvider.TWEET_URI, null);
				return Uri.withAppendedPath(uri, Long.toString(insertedId));
			}
			else
			{
				Log.d("ScrollLockDb", "Insert error for single tweet");
				return uri;
			}
		}
		}
		throw new IllegalArgumentException("Bulk insert -- Invalid URI:" + uri);
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] insertValues)
	{
		int insertedValues = -1;
		switch (uriMatcher.match(uri))
		{
		case TWEET_TIMELINE_QUERY:
		{
			SQLiteDatabase writableDatabase = null;

			try
			{
				writableDatabase = tweetDatabase.getWritableDatabase();
				writableDatabase.beginTransaction();

				for (ContentValues insertValue : insertValues)
				{
					writableDatabase.insert(TWEET_TABLE_NAME, null, insertValue);
				}

				writableDatabase.setTransactionSuccessful();

				getContext().getContentResolver().notifyChange(uri, null);
				insertedValues = insertValues.length;
			}
			catch (Exception e)
			{
				Log.d("ScrollLockDb", e.getClass().toString(), e);
			}
			finally
			{
				if (writableDatabase != null)
					writableDatabase.endTransaction();
			}
			break;
		}
		case USER_QUERY:
		{
			SQLiteDatabase localSQLiteDatabase = null;

			try
			{
				localSQLiteDatabase = tweetDatabase.getWritableDatabase();
				localSQLiteDatabase.beginTransaction();

				for (ContentValues insertValue : insertValues)
				{
					localSQLiteDatabase.insert(USER_TABLE_NAME, null, insertValue);
				}

				localSQLiteDatabase.setTransactionSuccessful();

				getContext().getContentResolver().notifyChange(TweetProvider.TWEET_URI, null);
				insertedValues = insertValues.length;
			}
			catch (Exception e)
			{
				Log.d("ScrollLockDb", e.getClass().toString(), e);
			}
			finally
			{
				if (localSQLiteDatabase != null)
					localSQLiteDatabase.endTransaction();
			}
			break;
		}
		case INVALID_URI:
		{
			throw new IllegalArgumentException("Bulk insert -- Invalid URI:" + uri);
		}
		}
		return insertedValues;
	}

	@Override
	public int delete(Uri uri, String s, String[] strings)
	{
		int deletedValues = 0;
		switch (uriMatcher.match(uri))
		{
		case TWEET_TIMELINE_QUERY:
		{
			SQLiteDatabase writableDatabase = null;

			try
			{
				writableDatabase = tweetDatabase.getWritableDatabase();
				writableDatabase.beginTransaction();

				deletedValues = writableDatabase.delete(TWEET_TABLE_NAME, TWEET_ID + " IN (SELECT " + TWEET_ID + " FROM "
						+ TWEET_TABLE_NAME + " ORDER BY " + TWEET_ID + " ASC LIMIT 200)", null);

				writableDatabase.setTransactionSuccessful();

				getContext().getContentResolver().notifyChange(uri, null);
			}
			catch (Exception e)
			{
				Log.d("ScrollLockDb", e.getClass().toString(), e);
			}
			finally
			{
				if (writableDatabase != null)
					writableDatabase.endTransaction();
			}
			break;
		}
		}
		return deletedValues;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String s, String[] strings)
	{
		return 0;
	}

	class TweetDatabase extends SQLiteOpenHelper
	{
		private static final int DATABASE_VERSION = 1;
		private static final String DATABASE_NAME = "TweetDictionaryDb";

		public TweetDatabase(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			try
			{
				db.execSQL("CREATE TABLE " + TWEET_TABLE_NAME + 
						" (" + 
						TWEET_ID + "  BIGINT CLUSTERED PRIMARY KEY NOT NULL," + 
						TWEET_USER_ID + "  BIGINT NOT NULL," + 
						TWEET_TEXT + " NVARCHAR(128) NOT NULL," + 
						TWEET_CREATED_AT + " NVARCHAR(50) NOT NULL," + 
						TWEET_USERNAME + " NVARCHAR(128) NOT NULL, " + 
						TWEET_PROFILE_PIC_URL + " NVARCHAR(1024) NOT NULL," +
						TWEET_RETWEET_COUNT + " INT NOT NULL, " + 
						TWEET_RETWEET_PROFILE_PIC_URL + " NVARCHAR(1024), " + 
						TWEET_RETWEETED_BY_USER_ID + " BIGINT, " + 
						TWEET_RETWEETED_BY_USERNAME + " NVARCHAR(128) " + 
						");");
				db.execSQL("CREATE TABLE " + USER_TABLE_NAME + " (" + USER_USER_ID + "  BIGINT PRIMARY KEY NOT NULL, "
						+ USER_PROFILE_PIC + " BLOB NOT NULL" + ");");
			}
			catch (Exception e)
			{
				Log.e("ScrollLockDb", e.getClass().toString(), e);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			try
			{
				Log.w("db", "Upgrading database from version " + oldVersion + " to " + newVersion
						+ ", which will destroy all old data");
				delete(db);
				onCreate(db);
			}
			catch (Exception e)
			{
				Log.e("ScrollLockDb", e.getClass().toString(), e);
			}
		}

		public void refreshDb()
		{
			SQLiteDatabase database = getWritableDatabase();
			delete(database);
			onCreate(database);
		}

		private void delete(SQLiteDatabase db)
		{
			try
			{
				db.execSQL("DROP TABLE IF EXISTS " + TWEET_TABLE_NAME);
				db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
			}
			catch (Exception e)
			{
				Log.e("ScrollLockDb", e.getClass().toString(), e);
			}
		}
	}
}
