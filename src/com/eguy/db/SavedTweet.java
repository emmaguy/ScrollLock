package com.eguy.db;

public class SavedTweet
{
    private String tweetText;
    private String createdAt;
    private long tweetUserId;

    public SavedTweet( String tweetText, String createdAt, long tweetUserId)
    {
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
}
