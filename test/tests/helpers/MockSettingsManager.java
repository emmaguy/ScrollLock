package tests.helpers;
import dev.emmaguy.twitterclient.IContainSettings;

public class MockSettingsManager implements IContainSettings
{
	private long maxId;
	private long sinceId;
	private long bottomOfGapId = -1;
	
	@Override
	public long getTweetSinceId()
	{
		return sinceId;
	}

	@Override
	public void setTweetSinceId(long sinceId)
	{
		this.sinceId = sinceId;
	}

	@Override
	public long getTweetMaxId()
	{
		return this.maxId; 
	}

	@Override
	public void setTweetMaxId(long maxId)
	{
		this.maxId = maxId;
	}

	@Override
	public boolean credentialsAvailable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void saveTokenAndSecret(String token, String tokenSecret)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveUserTokenAndSecret(String token, String secret)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveUsernameAndUserId(String userName, String userId)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getToken()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTokenSecret()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserToken()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserTokenSecret()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTweetBottomOfGapId()
	{
		return bottomOfGapId;
	}

	@Override
	public void setTweetBottomOfGapId(long oldestTweetId)
	{
		this.bottomOfGapId = oldestTweetId;
	}

	@Override
	public int getTweetPosition()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTweetPosition(int position)
	{
		// TODO Auto-generated method stub
		
	}	
}
