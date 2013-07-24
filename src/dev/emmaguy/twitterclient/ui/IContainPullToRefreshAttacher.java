package dev.emmaguy.twitterclient.ui;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public interface IContainPullToRefreshAttacher {
    PullToRefreshAttacher getRefreshAttacher();
}