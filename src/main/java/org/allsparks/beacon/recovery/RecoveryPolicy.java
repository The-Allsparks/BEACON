package org.allsparks.beacon.recovery;

/**
 * Bounded retry specification. Phase 0 stores the policy; it does not execute
 * hardware recovery.
 */
public final class RecoveryPolicy {
    private final int maxAttempts;
    private final long initialBackoffNanos;
    private final long timeoutNanos;
    private final long cpuBudgetNanos;
    private final boolean allowed;

    public RecoveryPolicy(
            boolean allowed,
            int maxAttempts,
            long initialBackoffNanos,
            long timeoutNanos,
            long cpuBudgetNanos) {
        if (maxAttempts < 0) {
            throw new IllegalArgumentException("maxAttempts must be >= 0");
        }
        this.allowed = allowed;
        this.maxAttempts = maxAttempts;
        this.initialBackoffNanos = initialBackoffNanos;
        this.timeoutNanos = timeoutNanos;
        this.cpuBudgetNanos = cpuBudgetNanos;
    }

    public static RecoveryPolicy disabled() {
        return new RecoveryPolicy(false, 0, 0L, 0L, 0L);
    }

    public long backoffNanos(int attemptIndex) {
        if (attemptIndex < 0) {
            throw new IllegalArgumentException("attemptIndex must be >= 0");
        }
        long shift = Math.min(attemptIndex, 8);
        return initialBackoffNanos << shift;
    }

    public boolean isAttemptAllowed(int attemptIndex) {
        return allowed && attemptIndex < maxAttempts;
    }

    public boolean allowed() {
        return allowed;
    }

    public int maxAttempts() {
        return maxAttempts;
    }

    public long initialBackoffNanos() {
        return initialBackoffNanos;
    }

    public long timeoutNanos() {
        return timeoutNanos;
    }

    public long cpuBudgetNanos() {
        return cpuBudgetNanos;
    }
}
