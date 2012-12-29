package com.eguy.db;

public class ProcessedTweet
{
    private long tweetId;
    private String tweetText;
    private String createdAt;
    private long tweetUserId;
    private String username;
    private String profilePicUrl;

    public ProcessedTweet(long tweetId, String tweetText, String createdAt, long tweetUserId, String username, String profileUrl)
    {
        this.tweetId = tweetId;
        this.tweetText = tweetText;
        this.createdAt = createdAt;
        this.tweetUserId = tweetUserId;
        this.username = username;
        this.profilePicUrl = profileUrl;
    }

    public String getUsername()
    {
        return username;
    }

    public String getProfilePictureUrl()
    {
        return profilePicUrl;
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