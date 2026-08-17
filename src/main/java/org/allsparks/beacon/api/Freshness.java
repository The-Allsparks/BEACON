package org.allsparks.beacon.api;

/**
 * Freshness of one observation relative to a per-source policy.
 *
 * <p>Camera frames, I²C readings, and Driver Station packets do not share a
 * single timing budget.
 */
public enum Freshness {
    CURRENT,
    DELAYED,
    STALE,
    LOST,
    RECOVERING,
    UNSTABLE,
    UNKNOWN
}
