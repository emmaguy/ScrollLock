package com.eguy;

public class Tweet
{
    private String tweetText;
    private String createdAt;
    private String postedByUsername;

    public Tweet(String tweetText, String createdAt, String postedByUsername)
    {
        this.tweetText = tweetText;
        this.createdAt = createdAt;
        this.postedByUsername = postedByUsername;
    }

    public String getTweetText()
    {
        return tweetText;
    }

    public String getPostedByUsername()
    {
        return postedByUsername;
    }

    public String getTweetCreatedAt()
    {
        return createdAt;
    }
}
