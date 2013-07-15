package dev.emmaguy.twitterclient.timeline;

import java.util.List;

public interface IBuildTimelineUpdates {
    TimelineUpdate build(List<twitter4j.Status> statuses);
}
