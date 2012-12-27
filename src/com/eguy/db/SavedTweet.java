package com.eguy.db;

public class SavedTweet
{
    private long tweetId;
    private String tweetText;
    private String createdAt;

    public SavedTweet(long tweetId, String tweetText, String createdAt)
    {
        this.tweetId = tweetId;
        this.tweetText = tweetText;
        this.createdAt = createdAt;
    }

    public long getId()
    {
        return tweetId;
    }

    public String getTweetText()
    {
        return tweetText;
    }

    public String getTweetCreatedAt()
    {
        return createdAt;
    }
}
