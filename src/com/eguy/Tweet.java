package com.eguy;

public class Tweet
{
    private String tweetText;
    private String createdAt;
    private String postedByUsername;
    private String profilePicUrl;

    public Tweet(String tweetText, String createdAt, String postedByUsername, String profilePicUrl)
    {
        this.tweetText = tweetText;
        this.createdAt = createdAt;
        this.postedByUsername = postedByUsername;
        this.profilePicUrl = profilePicUrl;
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

    public String getProfilePictureUrl()
    {
        return profilePicUrl;
    }
}
