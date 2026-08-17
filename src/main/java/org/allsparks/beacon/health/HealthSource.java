package org.allsparks.beacon.health;

import org.allsparks.beacon.api.HealthReport;
import org.allsparks.beacon.api.LinkHealth;
import org.allsparks.beacon.api.LinkId;

/**
 * Reports health for one link, device, data stream, or service.
 * Implementations must not perform recovery unless explicitly delegated.
 */
public interface HealthSource {
    LinkId id();

    /**
     * Snapshot for {@code nowNanos}. Must not invent latency or signal
     * measurements that the source cannot observe.
     */
    LinkHealth sample(long nowNanos);

    /**
     * Optional push of an externally constructed report. Default is ignore.
     */
    default void accept(HealthReport report) {
        // Manual sources override this. Probe-based sources ignore it.
    }
}
