package ru.synesis.kipod.facematch.ignite;

import java.util.Set;

import ru.synesis.kipod.event.FaceKipodEvent;

public class QueryFilter {

    private long since = -1;
    private long until = -1;
    private Set<Long> channels;

    public QueryFilter(long since, long until, Set<Long> channels) {
        this.since = since;
        this.until = until;
        this.channels = channels;
    }

    public boolean pass(FaceKipodEvent faceDetected) {
        if (since != -1 && since > faceDetected.start_time) {
            return false;
        }
        if (until != -1 && until < faceDetected.start_time) {
            return false;
        }
        if (channels != null && channels.size() > 0 && !channels.contains(faceDetected.channel)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QueryFilter [since=").append(since).append(", until=").append(until).append(", channels=")
                .append(channels).append("]");
        return builder.toString();
    }

}
