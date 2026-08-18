package org.allsparks.beacon;

/**
 * Central feature flags. Output-changing flags default to {@code false}.
 *
 * <p>Missing or stale observations must fail safe: intervention remains off
 * and sensing is reported as unavailable rather than invented.
 */
public final class BeaconFeatureFlags {

    private final boolean phase0Vocabulary;
    private final boolean phase1ManualReports;
    private final boolean phase2Preflight;
    private final boolean phase3EventHistory;
    private final boolean phase4AdvisoryShadow;
    private final boolean phase5DrivetrainSafeStop;
    private final boolean phase6MimicSafeState;
    private final boolean phase7BoundedRecovery;
    private final boolean phase8DegradedOperation;
    private final boolean phase9CollisionGuard;
    private final boolean phase10PostMatch;

    private BeaconFeatureFlags(Builder builder) {
        this.phase0Vocabulary = builder.phase0Vocabulary;
        this.phase1ManualReports = builder.phase1ManualReports;
        this.phase2Preflight = builder.phase2Preflight;
        this.phase3EventHistory = builder.phase3EventHistory;
        this.phase4AdvisoryShadow = builder.phase4AdvisoryShadow;
        this.phase5DrivetrainSafeStop = builder.phase5DrivetrainSafeStop;
        this.phase6MimicSafeState = builder.phase6MimicSafeState;
        this.phase7BoundedRecovery = builder.phase7BoundedRecovery;
        this.phase8DegradedOperation = builder.phase8DegradedOperation;
        this.phase9CollisionGuard = builder.phase9CollisionGuard;
        this.phase10PostMatch = builder.phase10PostMatch;
    }

    /** Safe defaults: Phase 0 on; all later phases off. */
    public static BeaconFeatureFlags defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isPhase0Vocabulary() {
        return phase0Vocabulary;
    }

    public boolean isPhase1ManualReports() {
        return phase1ManualReports;
    }

    public boolean isPhase2Preflight() {
        return phase2Preflight;
    }

    public boolean isPhase3EventHistory() {
        return phase3EventHistory;
    }

    public boolean isPhase4AdvisoryShadow() {
        return phase4AdvisoryShadow;
    }

    public boolean isPhase5DrivetrainSafeStop() {
        return phase5DrivetrainSafeStop;
    }

    public boolean isPhase6MimicSafeState() {
        return phase6MimicSafeState;
    }

    public boolean isPhase7BoundedRecovery() {
        return phase7BoundedRecovery;
    }

    public boolean isPhase8DegradedOperation() {
        return phase8DegradedOperation;
    }

    public boolean isPhase9CollisionGuard() {
        return phase9CollisionGuard;
    }

    public boolean isPhase10PostMatch() {
        return phase10PostMatch;
    }

    /** True if any feature that may change actuators or recover hardware is enabled. */
    public boolean isAnyInterventionEnabled() {
        return phase5DrivetrainSafeStop
                || phase6MimicSafeState
                || phase7BoundedRecovery
                || phase9CollisionGuard;
    }

    public static final class Builder {
        private boolean phase0Vocabulary = true;
        private boolean phase1ManualReports = false;
        private boolean phase2Preflight = false;
        private boolean phase3EventHistory = false;
        private boolean phase4AdvisoryShadow = false;
        private boolean phase5DrivetrainSafeStop = false;
        private boolean phase6MimicSafeState = false;
        private boolean phase7BoundedRecovery = false;
        private boolean phase8DegradedOperation = false;
        private boolean phase9CollisionGuard = false;
        private boolean phase10PostMatch = false;

        public Builder phase0Vocabulary(boolean value) {
            this.phase0Vocabulary = value;
            return this;
        }

        public Builder phase1ManualReports(boolean value) {
            this.phase1ManualReports = value;
            return this;
        }

        public Builder phase2Preflight(boolean value) {
            this.phase2Preflight = value;
            return this;
        }

        public Builder phase3EventHistory(boolean value) {
            this.phase3EventHistory = value;
            return this;
        }

        public Builder phase4AdvisoryShadow(boolean value) {
            this.phase4AdvisoryShadow = value;
            return this;
        }

        public Builder phase5DrivetrainSafeStop(boolean value) {
            this.phase5DrivetrainSafeStop = value;
            return this;
        }

        public Builder phase6MimicSafeState(boolean value) {
            this.phase6MimicSafeState = value;
            return this;
        }

        public Builder phase7BoundedRecovery(boolean value) {
            this.phase7BoundedRecovery = value;
            return this;
        }

        public Builder phase8DegradedOperation(boolean value) {
            this.phase8DegradedOperation = value;
            return this;
        }

        public Builder phase9CollisionGuard(boolean value) {
            this.phase9CollisionGuard = value;
            return this;
        }

        public Builder phase10PostMatch(boolean value) {
            this.phase10PostMatch = value;
            return this;
        }

        public BeaconFeatureFlags build() {
            return new BeaconFeatureFlags(this);
        }
    }

    /** Phase 0 plus manual reporting. No output intervention. */
    public static BeaconFeatureFlags manualReports() {
        return builder().phase1ManualReports(true).build();
    }

    /** Phase 0–2: manual reports and preflight. No output intervention. */
    public static BeaconFeatureFlags preflight() {
        return builder().phase1ManualReports(true).phase2Preflight(true).build();
    }
}
