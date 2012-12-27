package com.eguy.db;

public class SavedTweet
{
    private long tweetId;
    private String tweetText;
    private String createdAt;
    private long tweetUserId;

    public SavedTweet(long tweetId, String tweetText, String createdAt, long tweetUserId)
    {
        this.tweetId = tweetId;
        this.tweetText = tweetText;
        this.createdAt = createdAt;
        this.tweetUserId = tweetUserId;
    }

    public String getTweetText()
    {
        return tweetText;
    }

    public String getTweetCreatedAt()
    {
        return createdAt;
    }

    public long getTweetUserId()
    {
        return tweetUserId;
    }

    public long getTweetId()
    {
        return tweetId;
    }
}