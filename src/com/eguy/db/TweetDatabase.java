package com.eguy.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.eguy.Tweet;

import java.util.ArrayList;
import java.util.List;

public class TweetDatabase extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "TweetDictionaryDb";
    private static final int DATABASE_VERSION = 1;

    private static final String TWEET_TABLE_NAME = "JsonTweet";

    // JsonTweet table columns
    private static final String TWEET_ID = "TweetId";
    private static final String TWEET_TEXT = "Text";
    private static final String TWEET_USERID = "UserId";
    private static final String TWEET_CREATED_AT = "CreatedAt";

    // User table columns
    private static final String USER_TABLE_NAME = "User";
    private static final String USER_USERID = "UserId";
    private static final String USER_USERNAME = "Username";
    private static final String USER_PROFILE_PIC_URL = "ProfilePicture";

    public TweetDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<Tweet> getTweets()
    {
        List<Tweet> tweets = new ArrayList<Tweet>();
        SQLiteDatabase database = null;
        Cursor cur = null;

        try
        {
            database = getReadableDatabase();
            cur = database.rawQuery("SELECT " + TWEET_TEXT + "," + TWEET_CREATED_AT +  "," + USER_USERNAME + "," + USER_PROFILE_PIC_URL + " FROM " + TWEET_TABLE_NAME +
                    " t INNER JOIN " + USER_TABLE_NAME + " u ON u." + USER_USERID + " = t." + TWEET_USERID
                    + " ORDER BY t." + TWEET_ID + " DESC ", null);

            if (cur.moveToFirst())
            {
                do
                {
                    tweets.add(new Tweet(cur.getString(0), cur.getString(1), cur.getString(2), cur.getString(3)));
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

    public void saveUsers(List<SavedUser> users)
    {
        if (users.size() <= 0)
        {
            Log.i("db", "No users to save, returning");
            return;
        }

        SQLiteDatabase database = null;
        try
        {
            database = getWritableDatabase();
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(database, USER_TABLE_NAME);

            final int userId = ih.getColumnIndex(USER_USERID);
            final int username = ih.getColumnIndex(USER_USERNAME);
            final int profilePicture = ih.getColumnIndex(USER_PROFILE_PIC_URL);

            try
            {
                for (SavedUser user : users)
                {
                    ih.prepareForInsert();

                    ih.bind(userId, user.getUserId());
                    ih.bind(username, user.getUsername());
                    ih.bind(profilePicture, user.getProfilePictureUrl());

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
            final int tweetId = ih.getColumnIndex(TWEET_ID);
            final int timestamp = ih.getColumnIndex(TWEET_CREATED_AT);

            try
            {
                for (SavedTweet tweet : tweetsToSave)
                {
                    ih.prepareForInsert();

                    ih.bind(tweetId, tweet.getTweetId());
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
                    " ("+ TWEET_ID + "  BIGINT PRIMARY KEY NOT NULL," +
                    TWEET_USERID + "  BIGINT NOT NULL," +
                    TWEET_TEXT + " NVARCHAR(128) NOT NULL," +
                    TWEET_CREATED_AT + " NVARCHAR(50) NOT NULL" +
                    ");");
            db.execSQL("CREATE TABLE " + USER_TABLE_NAME +
                    " (" + USER_USERID + "  BIGINT PRIMARY KEY NOT NULL," +
                    USER_USERNAME + "  NVARCHAR(128) NOT NULL, " +
                    USER_PROFILE_PIC_URL + " NVARCHAR(1024)" +
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