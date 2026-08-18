package org.allsparks.beacon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.allsparks.beacon.adapters.future.SystemCoreAdapterBoundary;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.HealthReport;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.api.LinkState;
import org.allsparks.beacon.clock.FakeClock;
import org.allsparks.beacon.preflight.PreflightFinding;
import org.allsparks.beacon.preflight.PreflightStatus;
import org.allsparks.beacon.recovery.RecoveryPolicy;
import org.junit.jupiter.api.Test;

class BeaconSessionTest {
    @Test
    void defaultsDisableIntervention() {
        BeaconSession session = BeaconSession.create();
        assertFalse(session.isInterventionEnabled());
        assertTrue(session.flags().isPhase0Vocabulary());
        assertFalse(session.flags().isPhase1ManualReports());
        assertFalse(SystemCoreAdapterBoundary.isAvailable());
    }

    @Test
    void phase0StoresReportsWithoutLogging() {
        FakeClock clock = new FakeClock(1L);
        BeaconSession session = new BeaconSession(BeaconFeatureFlags.defaults(), clock, 16);
        session.report(HealthReport.healthy(
                LinkId.of("batteryTelemetry"), FailureDomain.ELECTRICAL, 1L, "AMPER"));
        assertEquals(1, session.snapshot().size());
        assertEquals(0, session.logger().size());
        assertEquals(LinkState.HEALTHY, session.snapshot().get(0).state());
    }

    @Test
    void timestampZeroIsUnknownNotHealthy() {
        FakeClock clock = new FakeClock(1L);
        BeaconSession session = new BeaconSession(BeaconFeatureFlags.defaults(), clock, 16);
        session.report(HealthReport.healthy(
                LinkId.of("batteryTelemetry"), FailureDomain.ELECTRICAL, 0L, "AMPER"));
        assertEquals(LinkState.UNKNOWN, session.snapshot().get(0).state());
        assertEquals(0, session.logger().size());
    }

    @Test
    void phase1LogsManualReports() {
        FakeClock clock = new FakeClock(1L);
        BeaconSession session = new BeaconSession(BeaconFeatureFlags.manualReports(), clock, 16);
        session.report(HealthReport.healthy(
                LinkId.of("localization"), FailureDomain.SOFTWARE_LOOP, 1L, "Pedro"));
        assertEquals(1, session.logger().size());
        assertEquals(LinkState.HEALTHY, session.snapshot().get(0).state());
    }

    @Test
    void recoveryPolicyBackoffIsBounded() {
        RecoveryPolicy policy = new RecoveryPolicy(true, 3, 10L, 1_000L, 2_000_000L);
        assertTrue(policy.isAttemptAllowed(2));
        assertFalse(policy.isAttemptAllowed(3));
        assertEquals(40L, policy.backoffNanos(2));
        assertFalse(RecoveryPolicy.disabled().allowed());
    }

    @Test
    void preflightFindingRequiresExplanation() {
        PreflightFinding finding = new PreflightFinding(
                LinkId.of("expansionHub"),
                false,
                PreflightStatus.READY_DEGRADED,
                "Optional Expansion Hub is absent");
        assertEquals(PreflightStatus.READY_DEGRADED, finding.status());
    }
}
