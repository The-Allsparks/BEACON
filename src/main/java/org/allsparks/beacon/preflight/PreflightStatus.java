package org.allsparks.beacon.preflight;

/** Pre-match inspection outcome. Every result must explain why. */
public enum PreflightStatus {
    READY,
    READY_DEGRADED,
    NOT_READY,
    UNKNOWN
}
