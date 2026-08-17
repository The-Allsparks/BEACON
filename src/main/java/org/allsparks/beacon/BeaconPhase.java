package org.allsparks.beacon;

/**
 * Development phases for BEACON. Higher phases build on lower phases and are
 * disabled by default until acceptance criteria are met.
 *
 * <p>Phases 0–4 must never command hardware. Phase 5+ may change outputs only
 * when an explicit feature flag is enabled after review.
 */
public enum BeaconPhase {
    /** Identifiers, health states, fake clocks, and documentation only. */
    PHASE_0_VOCABULARY,
    /** Manual health reports from robot code and sibling libraries. */
    PHASE_1_MANUAL_REPORTS,
    /** Pre-match required/optional capability inspection. */
    PHASE_2_PREFLIGHT,
    /** Bounded rolling timeline of health and related events. */
    PHASE_3_EVENT_HISTORY,
    /** Advisory correlation and shadow safe-state evaluation. */
    PHASE_4_ADVISORY_SHADOW,
    /** Teleop drivetrain-only communication safe stop, if freshness is proven. */
    PHASE_5_DRIVETRAIN_SAFE_STOP,
    /** MIMIC communication-loss safe-state requests. */
    PHASE_6_MIMIC_SAFE_STATE,
    /** Bounded local recovery of low-risk resources. */
    PHASE_7_BOUNDED_RECOVERY,
    /** Documented degraded-operation contracts. */
    PHASE_8_DEGRADED_OPERATION,
    /** Optional local collision guard. Experimental. */
    PHASE_9_COLLISION_GUARD,
    /** Post-match log correlation tooling. */
    PHASE_10_POST_MATCH
}
