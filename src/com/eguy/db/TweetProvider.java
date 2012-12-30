package com.eguy.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.SparseArray;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

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

    public static final String USER_TABLE_NAME = "User";
    public static final String USER_USER_ID = "UserId";
    public static final String USER_PROFILE_PIC = "ProfilePicture";

    private static final UriMatcher uriMatcher;
    private static final SparseArray<String> types;

    static
    {
        uriMatcher = new UriMatcher(0);
        uriMatcher.addURI(AUTHORITY, TWEET_TABLE_NAME, TWEET_TIMELINE_QUERY);
        uriMatcher.addURI(AUTHORITY, USER_TABLE_NAME, USER_QUERY);

        types = new SparseArray<String>();
        types.put(
                TWEET_TIMELINE_QUERY,
                "vnd.android.cursor.dir/vnd." +
                        AUTHORITY + "." +
                        TWEET_TEXT);

        types.put(
                USER_QUERY,
                "vnd.android.cursor.item/vnd." +
                        AUTHORITY + "." +
                        USER_TABLE_NAME);
    }

    public static final String SCHEME = "content";
    public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY);

    public static Uri TWEET_URI = Uri.withAppendedPath(CONTENT_URI, TWEET_TABLE_NAME);
    public static Uri USER_URI = Uri.withAppendedPath(CONTENT_URI, USER_TABLE_NAME);

    @Override
    public boolean onCreate()
    {
        tweetDatabase = new TweetDatabase(getContext());
        SQLiteDatabase db = tweetDatabase.getWritableDatabase();
        db.execSQL("DELETE FROM " + TWEET_TABLE_NAME + " WHERE " + TWEET_TEXT + " LIKE 'Generated Tweet:%'");

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
            cur = database.rawQuery("SELECT t." +
                    TWEET_USER_ID + "," +
                    TWEET_ID + "," +
                    TWEET_TEXT + "," +
                    TWEET_CREATED_AT + "," +
                    TWEET_USERNAME + "," +
                    TWEET_PROFILE_PIC_URL + "," +
                    USER_PROFILE_PIC +
                    " FROM " + TWEET_TABLE_NAME +
                    " t LEFT JOIN " + USER_TABLE_NAME +
                    " u ON t." + TWEET_USER_ID + " = u." + USER_USER_ID +
                    " ORDER BY t." + TWEET_ID + " DESC ", null);
            cur.setNotificationUri(getContext().getContentResolver(), uri);
        } catch (Exception ex)
        {
            Log.e("db", ex.toString());
        }
        return cur;
    }

    @Override
    public String getType(Uri uri)
    {
        return types.get(uriMatcher.match(uri));
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues)
    {
        switch (uriMatcher.match(uri))
        {
            case USER_QUERY:
                SQLiteDatabase writableDatabase = tweetDatabase.getWritableDatabase();

                long id = writableDatabase.insert(USER_TABLE_NAME, null, contentValues);

                if (-1 != id)
                {
                    getContext().getContentResolver().notifyChange(TweetProvider.TWEET_URI, null);
                    return Uri.withAppendedPath(uri, Long.toString(id));
                }
                else
                {
                    Log.e("ScrollLockDb", "Insert error for userid/profile picture");
                    return uri;
                }
            case TWEET_TIMELINE_QUERY:
                SQLiteDatabase sqldb = tweetDatabase.getWritableDatabase();

                long insertedId = sqldb.insert(TWEET_TABLE_NAME, null, contentValues);
                if (-1 != insertedId)
                {
                    getContext().getContentResolver().notifyChange(TweetProvider.TWEET_URI, null);
                    return Uri.withAppendedPath(uri, Long.toString(insertedId));
                }
                else
                {
                    Log.e("ScrollLockDb", "Insert error for single tweet");
                    return uri;
                }
        }
        throw new IllegalArgumentException("Bulk insert -- Invalid URI:" + uri);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] insertValues)
    {
        switch (uriMatcher.match(uri))
        {
            case TWEET_TIMELINE_QUERY:

                try
                {
                    SQLiteDatabase localSQLiteDatabase = tweetDatabase.getWritableDatabase();
                    localSQLiteDatabase.beginTransaction();

                    for (ContentValues insertValue : insertValues)
                    {
                        localSQLiteDatabase.insert(TWEET_TABLE_NAME, null, insertValue);
                    }

                    localSQLiteDatabase.setTransactionSuccessful();
                    localSQLiteDatabase.endTransaction();
                    localSQLiteDatabase.close();

                    getContext().getContentResolver().notifyChange(uri, null);
                    return insertValues.length;
                } catch (Exception e)
                {
                    Log.e("ScrollLockDb", e.getClass().toString(), e);
                }

            case USER_QUERY:
                try
                {
                    SQLiteDatabase localSQLiteDatabase = tweetDatabase.getWritableDatabase();
                    localSQLiteDatabase.beginTransaction();

                    for (ContentValues insertValue : insertValues)
                    {
                        localSQLiteDatabase.insert(USER_TABLE_NAME, null, insertValue);
                    }

                    localSQLiteDatabase.setTransactionSuccessful();
                    localSQLiteDatabase.endTransaction();
                    localSQLiteDatabase.close();

                    getContext().getContentResolver().notifyChange(TweetProvider.TWEET_URI, null);
                    return insertValues.length;
                } catch (Exception e)
                {
                    Log.e("ScrollLockDb", e.getClass().toString(), e);
                }

            case INVALID_URI:
                throw new IllegalArgumentException("Bulk insert -- Invalid URI:" + uri);

        }
        return -1;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings)
    {
        return 0;
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
                        TWEET_USERNAME + "  NVARCHAR(128) NOT NULL, " +
                        TWEET_PROFILE_PIC_URL + " NVARCHAR(1024) NOT NULL" +
                        ");");
                db.execSQL("CREATE TABLE " + USER_TABLE_NAME +
                        " (" +
                        USER_USER_ID + "  BIGINT PRIMARY KEY NOT NULL, " +
                        USER_PROFILE_PIC + " BLOB NOT NULL" +
                        ");");
            } catch (Exception e)
            {
                Log.e("ScrollLockDb", e.getClass().toString(), e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            try
            {
                Log.w("db", "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
                delete(db);
                onCreate(db);
            } catch (Exception e)
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
            } catch (Exception e)
            {
                Log.e("ScrollLockDb", e.getClass().toString(), e);
            }
        }
    }
}
