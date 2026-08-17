package org.allsparks.beacon.api;

/**
 * Phase 0 health states. Advanced states are documented but not exposed until
 * their transitions are tested.
 *
 * <p>{@link #UNKNOWN} is the fail-safe default when evidence is missing.
 */
public enum LinkState {
    /** No trustworthy observation has been received. */
    UNKNOWN,
    /** Latest observation is within the source freshness window. */
    HEALTHY,
    /** Latest observation is older than the stale threshold but not lost. */
    STALE,
    /** Latest observation exceeded the lost threshold, or an explicit loss was reported. */
    LOST
}
