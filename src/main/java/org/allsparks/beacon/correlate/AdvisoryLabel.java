package org.allsparks.beacon.correlate;

/**
 * Advisory-only diagnoses. There is no jamming or malicious-interference label.
 * Prefer {@link #INSUFFICIENT_EVIDENCE} when more than one story fits.
 */
public enum AdvisoryLabel {
    PROBABLE_POWER_DISRUPTION,
    PROBABLE_ISOLATED_CAMERA_FAILURE,
    PROBABLE_EXPANSION_HUB_PATH_FAILURE,
    PROBABLE_LOOP_OVERRUN,
    INSUFFICIENT_EVIDENCE
}
