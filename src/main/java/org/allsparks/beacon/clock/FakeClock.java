package org.allsparks.beacon.clock;

/** Deterministic clock for unit tests. Does not wrap wall time. */
public final class FakeClock implements BeaconClock {
    private long nanos;

    public FakeClock() {
        this(0L);
    }

    public FakeClock(long initialNanos) {
        this.nanos = initialNanos;
    }

    @Override
    public long nanoTime() {
        return nanos;
    }

    public void setNanos(long value) {
        this.nanos = value;
    }

    public void advanceNanos(long delta) {
        this.nanos += delta;
    }

    public void advanceMillis(long millis) {
        this.nanos += millis * 1_000_000L;
    }
}
