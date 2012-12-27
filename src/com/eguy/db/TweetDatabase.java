package com.eguy.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TweetDatabase extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "TweetDictionaryDb";
    private static final int DATABASE_VERSION = 1;

    private static final String TWEET_TABLE_NAME = "Tweet";

    // Tweet table columns
    private static final String TWEET_ID = "TweetId";
    private static final String TWEET_TEXT = "Text";
    private static final String TWEET_USERID = "UserId";
    private static final String TWEET_CREATED_AT = "CreatedAt";

    // User table columns
    private static final String USER_TABLE_NAME = "User";
    private static final String USER_USERID = "UserId";
    private static final String USER_PROFILE_PIC = "ProfilePicture";

    public TweetDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<SavedTweet> getAllSavedTweets()
    {
        List<SavedTweet> tweets = new ArrayList<SavedTweet>();
        String[] columns = {TWEET_TEXT, TWEET_CREATED_AT, TWEET_USERID};

        SQLiteDatabase database = null;
        Cursor cur = null;
        try
        {
            database = getReadableDatabase();
            cur = database.query(TWEET_TABLE_NAME, columns, null, null, null, null, TWEET_CREATED_AT);

            if (cur.moveToFirst())
            {
                do
                {
                    tweets.add(new SavedTweet(cur.getString(0), cur.getString(1), cur.getLong(2)));
                }
                while (cur.moveToNext());
            }
        }
        catch (Exception ex)
        {
            Log.e("db", ex.toString());
        }
        finally
        {
            if (cur != null)
                cur.close();
            if (database != null)
                database.close();
        }
        return tweets;
    }

    public void saveTweets(List<SavedTweet> tweetsToSave)
    {
        if (tweetsToSave.size() <= 0)
        {
            Log.i("db", "No tweets to save, returning");
            return;
        }

        SQLiteDatabase database = null;
        try
        {
            database = getWritableDatabase();
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(database, TWEET_TABLE_NAME);

            final int text = ih.getColumnIndex(TWEET_TEXT);
            final int userId = ih.getColumnIndex(TWEET_USERID);
            final int timestamp = ih.getColumnIndex(TWEET_CREATED_AT);

            try
            {
                for (SavedTweet tweet : tweetsToSave)
                {
                    ih.prepareForInsert();

                    ih.bind(userId, tweet.getTweetUserId());
                    ih.bind(text, tweet.getTweetText());
                    ih.bind(timestamp, tweet.getTweetCreatedAt());

                    ih.execute();
                }
            } finally
            {
                ih.close();
            }
        } catch (Exception e)
        {
            Log.e("ScrollLockDb", e.getClass().toString(), e);
        }
        finally
        {
            if(database != null)
                database.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            db.execSQL("CREATE TABLE " + TWEET_TABLE_NAME +
                    " (" + TWEET_USERID + "  BIGINT PRIMARY KEY NOT NULL," +
                            TWEET_TEXT + " Definition VARCHAR(128) NOT NULL," +
                            TWEET_CREATED_AT + " Definition VARCHAR(50) NOT NULL" +
                            ");");
            db.execSQL("CREATE TABLE " + USER_TABLE_NAME +
                    " (" + USER_USERID + "  BIGINT PRIMARY KEY NOT NULL," +
                    USER_PROFILE_PIC + " BLOB NOT NULL" +
                    ");");
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
            Log.w("db", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
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