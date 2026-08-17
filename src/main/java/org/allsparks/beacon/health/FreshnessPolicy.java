package org.allsparks.beacon.health;

import java.util.Objects;
import org.allsparks.beacon.api.Freshness;
import org.allsparks.beacon.api.LinkState;

/**
 * Per-source freshness thresholds. Does not invent measurements.
 *
 * <p>Hysteresis uses {@code lostThresholdNanos} strictly greater than
 * {@code staleThresholdNanos}.
 */
public final class FreshnessPolicy {
    private final long delayedThresholdNanos;
    private final long staleThresholdNanos;
    private final long lostThresholdNanos;

    public FreshnessPolicy(long delayedThresholdNanos, long staleThresholdNanos, long lostThresholdNanos) {
        if (delayedThresholdNanos < 0 || staleThresholdNanos < 0 || lostThresholdNanos < 0) {
            throw new IllegalArgumentException("Thresholds must be >= 0");
        }
        if (delayedThresholdNanos > staleThresholdNanos || staleThresholdNanos > lostThresholdNanos) {
            throw new IllegalArgumentException("Thresholds must be delayed <= stale <= lost");
        }
        this.delayedThresholdNanos = delayedThresholdNanos;
        this.staleThresholdNanos = staleThresholdNanos;
        this.lostThresholdNanos = lostThresholdNanos;
    }

    public static FreshnessPolicy ofMillis(long delayedMs, long staleMs, long lostMs) {
        return new FreshnessPolicy(delayedMs * 1_000_000L, staleMs * 1_000_000L, lostMs * 1_000_000L);
    }

    public Freshness classify(long lastValidTimestampNanos, long nowNanos) {
        if (lastValidTimestampNanos <= 0L) {
            return Freshness.UNKNOWN;
        }
        long age = nowNanos - lastValidTimestampNanos;
        if (age < 0L) {
            return Freshness.UNKNOWN;
        }
        if (age <= delayedThresholdNanos) {
            return Freshness.CURRENT;
        }
        if (age <= staleThresholdNanos) {
            return Freshness.DELAYED;
        }
        if (age <= lostThresholdNanos) {
            return Freshness.STALE;
        }
        return Freshness.LOST;
    }

    public LinkState toLinkState(Freshness freshness) {
        Objects.requireNonNull(freshness, "freshness");
        switch (freshness) {
            case CURRENT:
            case DELAYED:
            case RECOVERING:
                return LinkState.HEALTHY;
            case STALE:
            case UNSTABLE:
                return LinkState.STALE;
            case LOST:
                return LinkState.LOST;
            case UNKNOWN:
            default:
                return LinkState.UNKNOWN;
        }
    }

    public long delayedThresholdNanos() {
        return delayedThresholdNanos;
    }

    public long staleThresholdNanos() {
        return staleThresholdNanos;
    }

    public long lostThresholdNanos() {
        return lostThresholdNanos;
    }
}
