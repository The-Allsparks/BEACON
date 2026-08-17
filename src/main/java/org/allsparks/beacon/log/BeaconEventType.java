package org.allsparks.beacon.log;

/** Event kinds recorded for a future TRACE-compatible logger. */
public enum BeaconEventType {
    HEALTH_TRANSITION,
    FRESHNESS,
    PREFLIGHT,
    FAILURE_DOMAIN_HINT,
    SAFE_STATE_REQUEST,
    SHADOW_SAFE_STATE,
    RECOVERY_ATTEMPT,
    RECOVERY_RESULT,
    AMPER_VOLTAGE,
    LOOP_TIMING,
    SUBSYSTEM_RESPONSE,
    LEASE_EXPIRED,
    RECOVERY_INHIBIT,
    MANUAL_REPORT
}
