package dev.emmaguy.twitterclient.timeline;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class TweetDateFormatter {
    public String getFormattedDateTime(String datetime) {
	Date dateTimeOfTweet;
	SimpleDateFormat desiredFormat = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat twitterFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
	try {
	    dateTimeOfTweet = twitterFormat.parse(datetime);
	} catch (ParseException e) {
	    Log.e("ScrollLock", e.getClass().toString(), e);
	    return datetime;
	}

	Calendar tweetDateTime = getTweetDateCalendar(dateTimeOfTweet);

	if (DateMatches(tweetDateTime, getToday()))
	    return desiredFormat.format(dateTimeOfTweet);

	if (DateMatches(tweetDateTime, getYesterday()))
	    return "Yesterday " + desiredFormat.format(dateTimeOfTweet);

	return formatDateWithTwoLetterContractions(dateTimeOfTweet);
    }

    private String formatDateWithTwoLetterContractions(Date dateTimeOfTweet) {
	SimpleDateFormat format = new SimpleDateFormat("d HH:mm");
	String date = format.format(dateTimeOfTweet);

	if (date.endsWith("1") && !date.endsWith("11")) {
	    format = new SimpleDateFormat("EE MMM d'st' HH:mm");
	} else if (date.endsWith("2") && !date.endsWith("12")) {
	    format = new SimpleDateFormat("EE MMM d'nd' HH:mm");
	} else if (date.endsWith("3") && !date.endsWith("13")) {
	    format = new SimpleDateFormat("EE MMM d'rd' HH:mm");
	} else {
	    format = new SimpleDateFormat("EE MMM d'th' HH:mm");
	}

	return format.format(dateTimeOfTweet);
    }

    private Calendar getTweetDateCalendar(Date dateTimeOfTweet) {
	Calendar tweetDateCalendar = Calendar.getInstance();
	tweetDateCalendar.setTime(dateTimeOfTweet);
	return tweetDateCalendar;
    }

    private Calendar getYesterday() {
	Calendar yesterday = getToday();
	yesterday.add(Calendar.DAY_OF_YEAR, -1);
	return yesterday;
    }

    private Calendar getToday() {
	Calendar today = Calendar.getInstance();
	DateFormat.getDateTimeInstance().setCalendar(today);
	return today;
    }

    private boolean DateMatches(Calendar cal1, Calendar cal2) {
	return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
		&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
