package org.allsparks.beacon.api;

/** Why a link is not {@link LinkState#HEALTHY}. Use {@link #NONE} when healthy. */
public enum LinkFailureReason {
    NONE,
    NEVER_OBSERVED,
    STALE_DATA,
    EXPLICIT_LOSS_REPORT,
    TIMEOUT,
    DEVICE_NOT_PRESENT,
    INVALID_READING,
    LOOP_OVERRUN,
    INSUFFICIENT_EVIDENCE,
    UNSUPPORTED_MEASUREMENT
}
