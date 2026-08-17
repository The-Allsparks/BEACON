package org.allsparks.beacon.lease;

/**
 * Prevents automatic restoration of a stale command after communication returns.
 * Does not command motors; callers apply the inhibit decision.
 */
public final class RecoveryInhibit {
    private final int requiredFreshObservations;
    private final long requiredStableNanos;
    private int consecutiveFresh;
    private long stableSinceNanos = -1L;
    private boolean inhibited = true;

    public RecoveryInhibit(int requiredFreshObservations, long requiredStableNanos) {
        if (requiredFreshObservations < 1) {
            throw new IllegalArgumentException("requiredFreshObservations must be >= 1");
        }
        if (requiredStableNanos < 0L) {
            throw new IllegalArgumentException("requiredStableNanos must be >= 0");
        }
        this.requiredFreshObservations = requiredFreshObservations;
        this.requiredStableNanos = requiredStableNanos;
    }

    public static RecoveryInhibit typical() {
        return new RecoveryInhibit(5, 250_000_000L);
    }

    public void enter() {
        inhibited = true;
        consecutiveFresh = 0;
        stableSinceNanos = -1L;
    }

    /**
     * @param nowNanos current time
     * @param fresh whether this observation is a verified fresh source packet
     * @param controlsNeutral whether sticks and triggers are inside the deadband
     * @return true when the inhibit is still active
     */
    public boolean update(long nowNanos, boolean fresh, boolean controlsNeutral) {
        if (!inhibited) {
            return false;
        }
        if (!fresh || !controlsNeutral) {
            consecutiveFresh = 0;
            stableSinceNanos = -1L;
            return true;
        }
        consecutiveFresh++;
        if (stableSinceNanos < 0L) {
            stableSinceNanos = nowNanos;
        }
        long stableFor = nowNanos - stableSinceNanos;
        if (consecutiveFresh >= requiredFreshObservations && stableFor >= requiredStableNanos) {
            inhibited = false;
            return false;
        }
        return true;
    }

    public boolean isInhibited() {
        return inhibited;
    }

    public int consecutiveFresh() {
        return consecutiveFresh;
    }
}
