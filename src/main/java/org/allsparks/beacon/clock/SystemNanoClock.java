package org.allsparks.beacon.clock;

/** Production clock backed by {@link System#nanoTime()}. */
public final class SystemNanoClock implements BeaconClock {
    @Override
    public long nanoTime() {
        return System.nanoTime();
    }
}
