package dev.emmaguy.twitterclient.twitterapi;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RateCalculator {
    private int numberOfRequestsPerWindow;
    private long windowLengthInMilliseconds;

    private List<Date> requestTimes;

    public RateCalculator(int numberOfRequestsPerWindow, int windowLengthInMinutes) {
	this.numberOfRequestsPerWindow = numberOfRequestsPerWindow;
	this.windowLengthInMilliseconds = TimeUnit.MILLISECONDS.convert(windowLengthInMinutes, TimeUnit.MINUTES);
	this.requestTimes = new LinkedList<Date>();
    }

    public void requestMade() {
	requestTimes.add(new Date());
    }

    public boolean canMakeRequest() {
	removeExpiredTimes();

	return requestTimes.size() < numberOfRequestsPerWindow;
    }

    private void removeExpiredTimes() {
	if (requestTimes.isEmpty())
	    return;

	// times are added in order of request so earliest will be at the front
	Date requestTime = requestTimes.get(0);
	long duration = new Date().getTime() - requestTime.getTime();
	if (duration > windowLengthInMilliseconds) {
	    requestTimes.remove(0);
	    removeExpiredTimes();
	}
    }
}