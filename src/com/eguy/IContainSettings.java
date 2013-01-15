package com.eguy;

public interface IContainSettings
{
	long getTweetSinceId();
    void setTweetSinceId(long sinceId);
	
	long getTweetMaxId();
    void setTweetMaxId(long maxId);
    
	long getTweetBottomOfGapId();
	void setTweetBottomOfGapId(long oldestTweetId);
    
    boolean credentialsAvailable();
    void saveTokenAndSecret(String token, String tokenSecret);
    void saveUserTokenAndSecret(String token, String secret);
    void saveUsernameAndUserId(String userName, String userId);
	
    String getToken();
    String getTokenSecret();
    String getUserToken();
    String getUserTokenSecret();
    String getUsername();
}
