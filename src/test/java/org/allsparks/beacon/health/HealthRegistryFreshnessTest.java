package org.allsparks.beacon.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.allsparks.beacon.BeaconFeatureFlags;
import org.allsparks.beacon.BeaconSession;
import org.allsparks.beacon.api.Confidence;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.HealthReport;
import org.allsparks.beacon.api.LinkFailureReason;
import org.allsparks.beacon.api.LinkHealth;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.api.LinkState;
import org.allsparks.beacon.clock.FakeClock;
import org.junit.jupiter.api.Test;

class HealthRegistryFreshnessTest {
    private static final LinkId CAMERA = LinkId.of("frontCamera");
    private static final LinkId HUB = LinkId.of("expansionHub");

    @Test
    void freshHealthyReportIsHealthy() {
        FakeClock clock = new FakeClock(1_000_000L);
        HealthRegistry registry = new HealthRegistry(clock);
        LinkHealth health = registry.report(HealthReport.healthy(
                CAMERA, FailureDomain.USB_CAMERA, clock.nanoTime(), "ViDAR"));
        assertEquals(LinkState.HEALTHY, health.state());
        assertEquals(1, health.consecutiveSuccesses());
        assertEquals(0, health.consecutiveFailures());
        assertFalse(BeaconSession.create().isInterventionEnabled());
        assertFalse(BeaconFeatureFlags.defaults().isAnyInterventionEnabled());
    }

    @Test
    void delayedWindowStillMapsToHealthy() {
        FakeClock clock = new FakeClock(1L);
        HealthRegistry registry = new HealthRegistry(clock);
        registry.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        clock.advanceMillis(50);
        assertEquals(LinkState.HEALTHY, registry.snapshot().get(0).state());
    }

    @Test
    void agePastStaleThresholdWithoutNewReport() {
        FakeClock clock = new FakeClock(1L);
        HealthRegistry registry = new HealthRegistry(clock);
        registry.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        clock.advanceMillis(81);
        LinkHealth health = registry.snapshot().get(0);
        assertEquals(LinkState.STALE, health.state());
        assertEquals(LinkFailureReason.STALE_DATA, health.reason());
        assertEquals(1, health.consecutiveSuccesses());
        assertEquals(0, health.consecutiveFailures());
    }

    @Test
    void agePastLostThresholdWithoutNewReport() {
        FakeClock clock = new FakeClock(1L);
        HealthRegistry registry = new HealthRegistry(clock);
        registry.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        clock.advanceMillis(251);
        LinkHealth health = registry.snapshot().get(0);
        assertEquals(LinkState.LOST, health.state());
        assertEquals(LinkFailureReason.TIMEOUT, health.reason());
        assertEquals(1, health.consecutiveSuccesses());
        assertEquals(0, health.consecutiveFailures());
    }

    @Test
    void explicitLossRemainsLostInsideCurrentWindow() {
        FakeClock clock = new FakeClock(1L);
        HealthRegistry registry = new HealthRegistry(clock);
        registry.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        registry.report(new HealthReport(
                CAMERA,
                FailureDomain.USB_CAMERA,
                LinkState.LOST,
                2L,
                "ViDAR",
                "pipeline stopped",
                LinkFailureReason.EXPLICIT_LOSS_REPORT,
                Confidence.of(0.8)));
        clock.advanceMillis(10);
        LinkHealth health = registry.snapshot().get(0);
        assertEquals(LinkState.LOST, health.state());
        assertEquals(LinkFailureReason.EXPLICIT_LOSS_REPORT, health.reason());
        assertEquals(1L, health.lastValidTimestampNanos());
    }

    @Test
    void consecutiveCountsAccumulateOnAcceptOnly() {
        FakeClock clock = new FakeClock(1L);
        HealthRegistry registry = new HealthRegistry(clock);
        registry.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        clock.advanceMillis(1);
        registry.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, clock.nanoTime(), "ViDAR"));
        assertEquals(2, registry.snapshot().get(0).consecutiveSuccesses());

        registry.report(new HealthReport(
                CAMERA,
                FailureDomain.USB_CAMERA,
                LinkState.STALE,
                clock.nanoTime(),
                "ViDAR",
                "old frame",
                LinkFailureReason.STALE_DATA,
                Confidence.of(0.5)));
        LinkHealth afterFailure = registry.snapshot().get(0);
        assertEquals(0, afterFailure.consecutiveSuccesses());
        assertEquals(1, afterFailure.consecutiveFailures());

        registry.report(new HealthReport(
                CAMERA,
                FailureDomain.USB_CAMERA,
                LinkState.LOST,
                clock.nanoTime(),
                "ViDAR",
                "no frames",
                LinkFailureReason.EXPLICIT_LOSS_REPORT,
                Confidence.of(0.9)));
        assertEquals(2, registry.snapshot().get(0).consecutiveFailures());
    }

    @Test
    void timestampZeroHealthyReportIsUnknown() {
        FakeClock clock = new FakeClock(1L);
        HealthRegistry registry = new HealthRegistry(clock);
        LinkHealth health = registry.report(HealthReport.healthy(
                CAMERA, FailureDomain.USB_CAMERA, 0L, "ViDAR"));
        assertEquals(LinkState.UNKNOWN, health.state());
        assertEquals(LinkFailureReason.NEVER_OBSERVED, health.reason());
    }

    @Test
    void futureTimestampIsUnknown() {
        FakeClock clock = new FakeClock(10L);
        HealthRegistry registry = new HealthRegistry(clock);
        LinkHealth health = registry.report(HealthReport.healthy(
                CAMERA, FailureDomain.USB_CAMERA, 50L, "ViDAR"));
        assertEquals(LinkState.UNKNOWN, health.state());
        assertEquals(LinkFailureReason.INSUFFICIENT_EVIDENCE, health.reason());
    }

    @Test
    void perSourcePolicyOverrideDoesNotChangeOthers() {
        FakeClock clock = new FakeClock(1L);
        HealthRegistry registry = new HealthRegistry(clock);
        registry.setFreshnessPolicy(CAMERA, FreshnessPolicy.ofMillis(5, 10, 20));
        registry.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        registry.report(HealthReport.healthy(HUB, FailureDomain.CONTROL_HUB_TO_EXPANSION_HUB, 1L, "robot"));
        clock.advanceMillis(15);
        assertEquals(LinkState.STALE, registry.get(CAMERA).get().state());
        assertEquals(LinkState.HEALTHY, registry.get(HUB).get().state());
    }
}
