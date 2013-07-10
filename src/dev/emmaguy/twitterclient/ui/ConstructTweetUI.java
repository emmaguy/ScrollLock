package dev.emmaguy.twitterclient.ui;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ConstructTweetUI {
    private ViewHolder holder;

    private int retweetCount;
    private long tweetUserUserId;
    private long retweetedByUserId;

    private String tweetCreatedAt;
    private String tweetUserUsername;
    private String tweetUserProfileUrl;
    private String retweetedByUsername;
    private String tweetText;
    private String retweetedByUserUrl;

    private byte[] tweetUserProfileImage;
    private byte[] retweetedByImage;

    private ContentResolver contentResolver;

    public ConstructTweetUI(ViewHolder holder, ContentResolver contentResolver, String tweetText,
	    String tweetCreatedAt, String tweetUserUsername, String tweetUserProfileUrl, byte[] tweetUserProfileImage,
	    int tweetRetweetCount, long tweetUserUserId, byte[] retweetedByImage, String retweetedByUsername,
	    long retweetedByUserId, String retweetedByUserUrl) {
	this.holder = holder;
	this.contentResolver = contentResolver;
	this.tweetText = tweetText;
	this.tweetCreatedAt = tweetCreatedAt;
	this.retweetCount = tweetRetweetCount;

	this.tweetUserUsername = tweetUserUsername;
	this.tweetUserProfileUrl = tweetUserProfileUrl;
	this.tweetUserProfileImage = tweetUserProfileImage;
	this.tweetUserUserId = tweetUserUserId;

	this.retweetedByImage = retweetedByImage;
	this.retweetedByUsername = retweetedByUsername;
	this.retweetedByUserId = retweetedByUserId;
	this.retweetedByUserUrl = retweetedByUserUrl;
    }

    public void Create() {
	if (retweetCount == 0) {
	    constructTweetWithNoRetweetNumber();
	} else {
	    if (retweetedByUsername == null || retweetedByUsername.isEmpty()) {
		constructTweetWithRetweetNumber();
	    } else {
		constructTweetWithRetweetNumberAndUsername();
	    }
	}
    }

    private void setOrRetrieveProfilePicture(long userId, String url, String username, byte[] image,
	    ImageView imageViewToUpdate) {
	if (image == null || image.length == 0) {
	    Log.d("ScrollLock", "No user image for user: " + username + " starting async task");

	    new DownloadImageTask(userId, contentResolver).execute(url);
	} else {
	    Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
	    imageViewToUpdate.setImageBitmap(bmp);
	}
    }

    private void setTweet(String username) {
	holder.TweetText.setText(tweetText);
	holder.Username.setText("@" + username);
	holder.CreatedAt.setText(new TweetDateFormatter().getFormattedDateTime(tweetCreatedAt));
    }

    private void constructTweetWithRetweetNumberAndUsername() {
	holder.RetweetInfo.setVisibility(View.VISIBLE);
	holder.ProfilePictureSmall.setVisibility(View.VISIBLE);

	setTweet(retweetedByUsername);
	setOrRetrieveProfilePicture(retweetedByUserId, retweetedByUserUrl, retweetedByUsername, retweetedByImage,
		holder.ProfilePicture);
	setOrRetrieveProfilePicture(tweetUserUserId, tweetUserProfileUrl, tweetUserUsername, tweetUserProfileImage,
		holder.ProfilePictureSmall);

	int otherRetweetersCount = retweetCount - 1;
	if (otherRetweetersCount > 0) {
	    holder.RetweetInfo.setText("Retweeted by " + tweetUserUsername + " and " + otherRetweetersCount
		    + " other(s)");
	} else {
	    holder.RetweetInfo.setText("Retweeted by " + tweetUserUsername);
	}
    }

    private void constructTweetWithRetweetNumber() {
	holder.RetweetInfo.setVisibility(View.VISIBLE);
	holder.RetweetInfo.setText("Retweets: " + retweetCount);

	holder.ProfilePictureSmall.setVisibility(View.GONE);

	setTweet(tweetUserUsername);
	setOrRetrieveProfilePicture(tweetUserUserId, tweetUserProfileUrl, tweetUserUsername, tweetUserProfileImage,
		holder.ProfilePicture);
    }

    private void constructTweetWithNoRetweetNumber() {
	holder.RetweetInfo.setVisibility(View.GONE);
	holder.ProfilePictureSmall.setVisibility(View.GONE);

	setTweet(tweetUserUsername);
	setOrRetrieveProfilePicture(tweetUserUserId, tweetUserProfileUrl, tweetUserUsername, tweetUserProfileImage,
		holder.ProfilePicture);
    }
}
