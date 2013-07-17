package dev.emmaguy.twitterclient;

public interface IContainSettings {
    long getTweetSinceId();

    void setTweetSinceId(long sinceId);

    long getTweetMaxId();

    void setTweetMaxId(long maxId);

    long getTweetBottomOfGapId();

    void setTweetBottomOfGapId(long oldestTweetId);

    boolean credentialsAvailable();

    void saveTokenAndSecret(String token, String tokenSecret);

    void saveUserTokenAndSecret(String token, String secret);

    void saveUsernameAndUserId(String userName, long userId);

    int getTweetPosition();

    void setTweetPosition(int position);

    String getToken();

    String getTokenSecret();

    String getUserToken();

    String getUserTokenSecret();

    String getUsername();

    void clearUserData();

    int getNumberOfTweetsToRequest();

    int getThemeResourceId();

    void setTheme(String value);

}
