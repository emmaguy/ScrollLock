package com.eguy.db;

public class SavedUser
{
    private long userId;
    private String username;
    private String profilePicUrl;

    public SavedUser(long userId, String username, String profilePicUrl)
    {
        this.userId = userId;
        this.username = username;
        this.profilePicUrl = profilePicUrl;
    }

    public long getUserId()
    {
        return userId;
    }

    public String getUsername()
    {
        return username;
    }

    public String getProfilePictureUrl()
    {
        return profilePicUrl;
    }
}
