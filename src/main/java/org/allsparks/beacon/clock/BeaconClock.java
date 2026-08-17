package org.allsparks.beacon.clock;

/**
 * Time source abstraction so tests can advance time without hardware.
 * Units are nanoseconds since an arbitrary origin (monotonic preferred).
 */
public interface BeaconClock {
    long nanoTime();
}
