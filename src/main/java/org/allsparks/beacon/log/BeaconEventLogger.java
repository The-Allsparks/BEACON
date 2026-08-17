package org.allsparks.beacon.log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/** Bounded rolling event history. Drops oldest events when full. */
public final class BeaconEventLogger {
    private final int capacity;
    private final Deque<BeaconEvent> events;
    private long dropped;

    public BeaconEventLogger(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be >= 1");
        }
        this.capacity = capacity;
        this.events = new ArrayDeque<>(capacity);
    }

    public void record(BeaconEvent event) {
        Objects.requireNonNull(event, "event");
        if (events.size() == capacity) {
            events.removeFirst();
            dropped++;
        }
        events.addLast(event);
    }

    public List<BeaconEvent> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(events));
    }

    public int size() {
        return events.size();
    }

    public int capacity() {
        return capacity;
    }

    public long droppedCount() {
        return dropped;
    }

    public void clear() {
        events.clear();
        dropped = 0L;
    }

    public String exportCsv() {
        StringBuilder builder = new StringBuilder("timestampNanos,type,linkId,domain,detail\n");
        for (BeaconEvent event : events) {
            builder.append(event.toCsvRow()).append('\n');
        }
        return builder.toString();
    }
}
