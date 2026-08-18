package org.allsparks.beacon.preflight;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.HealthReport;
import org.allsparks.beacon.api.LinkFailureReason;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.api.LinkState;
import org.allsparks.beacon.clock.FakeClock;
import org.allsparks.beacon.health.HealthRegistry;
import org.junit.jupiter.api.Test;

class PreflightInspectorTest {
    private static final LinkId CAMERA = LinkId.of("frontCamera");
    private static final LinkId HUB = LinkId.of("expansionHub");
    private static final LinkId LOCALIZATION = LinkId.of("localization");

    @Test
    void allRequiredHealthyIsReady() {
        FakeClock clock = new FakeClock(1L);
        HealthRegistry registry = new HealthRegistry(clock);
        registry.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        registry.report(HealthReport.healthy(LOCALIZATION, FailureDomain.SOFTWARE_LOOP, 1L, "Pedro"));
        PreflightReport report = PreflightInspector.evaluate(
                registry,
                Arrays.asList(PreflightExpectation.required(CAMERA), PreflightExpectation.required(LOCALIZATION)));
        assertEquals(PreflightStatus.READY, report.status());
        assertEquals(2, report.findings().size());
        assertFalse(report.findings().get(0).explanation().trim().isEmpty());
    }

    @Test
    void optionalAbsenceIsReadyDegraded() {
        FakeClock clock = new FakeClock(1L);
        HealthRegistry registry = new HealthRegistry(clock);
        registry.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        PreflightReport report = PreflightInspector.evaluate(
                registry,
                Arrays.asList(PreflightExpectation.required(CAMERA), PreflightExpectation.optional(HUB)));
        assertEquals(PreflightStatus.READY_DEGRADED, report.status());
        assertEquals(PreflightStatus.READY_DEGRADED, report.findings().get(1).status());
        assertTrue(report.findings().get(1).explanation().contains("expansionHub"));
    }

    @Test
    void requiredLostIsNotReady() {
        FakeClock clock = new FakeClock(1L);
        HealthRegistry registry = new HealthRegistry(clock);
        registry.report(new HealthReport(
                CAMERA,
                FailureDomain.USB_CAMERA,
                LinkState.LOST,
                1L,
                "ViDAR",
                "no frames",
                LinkFailureReason.EXPLICIT_LOSS_REPORT,
                org.allsparks.beacon.api.Confidence.of(0.9)));
        PreflightReport report = PreflightInspector.evaluate(
                registry, Collections.singletonList(PreflightExpectation.required(CAMERA)));
        assertEquals(PreflightStatus.NOT_READY, report.status());
        assertTrue(report.findings().get(0).explanation().contains("lost"));
    }

    @Test
    void requiredMissingEvidenceIsUnknownNotFabricatedNetworkFailure() {
        HealthRegistry registry = new HealthRegistry(new FakeClock(1L));
        PreflightReport report = PreflightInspector.evaluate(
                registry,
                Collections.singletonList(
                        PreflightExpectation.required(LinkId.of("driverStation"))));
        assertEquals(PreflightStatus.UNKNOWN, report.status());
        assertTrue(report.findings().get(0).explanation().contains("not treated as a Driver Station network failure"));
    }

    @Test
    void requiredStaleIsNotReady() {
        FakeClock clock = new FakeClock(1L);
        HealthRegistry registry = new HealthRegistry(clock);
        registry.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        clock.advanceMillis(81);
        PreflightReport report = PreflightInspector.evaluate(
                registry, Collections.singletonList(PreflightExpectation.required(CAMERA)));
        assertEquals(PreflightStatus.NOT_READY, report.status());
        assertTrue(report.findings().get(0).explanation().contains("stale"));
    }

    @Test
    void emptyExpectationsFailClosed() {
        HealthRegistry registry = new HealthRegistry(new FakeClock(1L));
        PreflightReport report = PreflightInspector.evaluate(registry, Collections.emptyList());
        assertEquals(PreflightStatus.UNKNOWN, report.status());
        assertFalse(report.findings().get(0).explanation().trim().isEmpty());
    }

    @Test
    void notReadyOutranksUnknownAndDegraded() {
        assertEquals(PreflightStatus.NOT_READY, PreflightInspector.worse(PreflightStatus.UNKNOWN, PreflightStatus.NOT_READY));
        assertEquals(PreflightStatus.UNKNOWN, PreflightInspector.worse(PreflightStatus.READY_DEGRADED, PreflightStatus.UNKNOWN));
        assertEquals(PreflightStatus.READY_DEGRADED, PreflightInspector.worse(PreflightStatus.READY, PreflightStatus.READY_DEGRADED));
    }
}
