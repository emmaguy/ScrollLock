package com.eguy.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

public class TweetDatabase extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TweetDictionaryDb";

    private static final String TWEET_TABLE_NAME = "Tweet";

    private static final String TWEET_ID = BaseColumns._ID;
    public static final String TWEET_USER_ID = "UserId";

    public static final String TWEET_TEXT = "Text";
    public static final String TWEET_CREATED_AT = "CreatedAt";
    public static final String TWEET_USERNAME = "Username";
    public static final String TWEET_PROFILE_PIC_URL = "ProfilePictureUrl";

    private static final String USER_TABLE_NAME = "User";
    private static final String USER_USER_ID = "UserId";
    public static final String USER_PROFILE_PIC = "ProfilePicture";

    public TweetDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getTweetsCursor()
    {
        Cursor cur = null;

        try
        {
            SQLiteDatabase database = getReadableDatabase();
            cur = database.rawQuery("SELECT t."+
                    TWEET_USER_ID + ","  +
                    TWEET_ID + "," +
                    TWEET_TEXT + "," +
                    TWEET_CREATED_AT + "," +
                    TWEET_USERNAME + "," +
                    TWEET_PROFILE_PIC_URL + "," +
                    USER_PROFILE_PIC +
                    " FROM " + TWEET_TABLE_NAME +
                    " t LEFT JOIN " + USER_TABLE_NAME + " u ON t." + TWEET_USER_ID + " = u." + USER_USER_ID +
                    " ORDER BY t." + TWEET_ID + " DESC ", null);
        } catch (Exception ex)
        {
            Log.e("db", ex.toString());
        }
        return cur;
    }

    public void saveTweets(List<ProcessedTweet> tweetsToSave)
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

            try
            {
                database.beginTransaction();
                for (ProcessedTweet tweet : tweetsToSave)
                {
                    ih.prepareForInsert();
                    ih.bind(ih.getColumnIndex(TWEET_ID), tweet.getTweetId());
                    ih.bind(ih.getColumnIndex(TWEET_USER_ID), tweet.getTweetUserId());
                    ih.bind(ih.getColumnIndex(TWEET_TEXT), tweet.getTweetText());
                    ih.bind(ih.getColumnIndex(TWEET_CREATED_AT), tweet.getTweetCreatedAt());
                    ih.bind(ih.getColumnIndex(TWEET_USERNAME), tweet.getUsername());
                    ih.bind(ih.getColumnIndex(TWEET_PROFILE_PIC_URL), tweet.getProfilePictureUrl());
                    ih.execute();
                }
                database.setTransactionSuccessful();
                database.endTransaction();
            } finally
            {
                ih.close();
            }
        } catch (Exception e)
        {
            Log.e("ScrollLockDb", e.getClass().toString(), e);
        } finally
        {
            if (database != null)
                database.close();
        }
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

    public void addUserProfilePicture(Bitmap profilePicture, long userId)
    {
        if(profilePicture == null)
            return;

        SQLiteDatabase database = null;
        try
        {
            database = getWritableDatabase();
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(database, USER_TABLE_NAME);

            final int colUserId = ih.getColumnIndex(USER_USER_ID);
            final int colProfilePic = ih.getColumnIndex(USER_PROFILE_PIC);

            ByteBuffer byteBuffer = ByteBuffer.allocate(profilePicture.getByteCount());
            profilePicture.copyPixelsToBuffer(byteBuffer);

            try
            {
                ih.prepareForInsert();
                ih.bind(colUserId, userId);
                ih.bind(colProfilePic, byteBuffer.array());
                ih.execute();
            }
            finally
            {
                ih.close();
            }
        } catch (Exception e)
        {
            Log.e("ScrollLockDb", e.getClass().toString(), e);
        } finally
        {
            if (database != null)
                database.close();
        }
    }
}