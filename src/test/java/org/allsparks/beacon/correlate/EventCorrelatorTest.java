package org.allsparks.beacon.correlate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import org.allsparks.beacon.api.Confidence;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.LinkFailureReason;
import org.allsparks.beacon.api.LinkHealth;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.api.LinkState;
import org.allsparks.beacon.log.BeaconEvent;
import org.allsparks.beacon.log.BeaconEventType;
import org.junit.jupiter.api.Test;

class EventCorrelatorTest {
    private static final LinkId CAMERA = LinkId.of("frontCamera");
    private static final LinkId HUB = LinkId.of("expansionHub");
    private static final LinkId BATTERY = LinkId.of("batteryTelemetry");
    private static final LinkId LOCALIZATION = LinkId.of("localization");

    @Test
    void emptySnapshotIsInsufficient() {
        AdvisoryReport report = EventCorrelator.evaluate(Collections.emptyList(), Collections.emptyList());
        assertEquals(AdvisoryLabel.INSUFFICIENT_EVIDENCE, report.label());
        assertFalse(report.confidence().isKnown());
        assertFalse(report.evidence().isEmpty());
        assertFalse(report.explanation().trim().isEmpty());
    }

    @Test
    void healthyLinksAreInsufficient() {
        AdvisoryReport report = EventCorrelator.evaluate(
                Collections.emptyList(), Collections.singletonList(healthy(CAMERA, FailureDomain.USB_CAMERA)));
        assertEquals(AdvisoryLabel.INSUFFICIENT_EVIDENCE, report.label());
        assertFalse(report.confidence().isKnown());
    }

    @Test
    void isolatedCameraLossIsProbableCamera() {
        AdvisoryReport report = EventCorrelator.evaluate(
                Collections.emptyList(), Collections.singletonList(lost(CAMERA, FailureDomain.USB_CAMERA)));
        assertEquals(AdvisoryLabel.PROBABLE_ISOLATED_CAMERA_FAILURE, report.label());
        assertEquals(EventCorrelator.ISOLATED, report.confidence());
        assertEquals(CAMERA, report.evidence().get(0).linkId());
    }

    @Test
    void isolatedHubLossIsProbableHubPath() {
        AdvisoryReport report = EventCorrelator.evaluate(
                Collections.emptyList(),
                Collections.singletonList(lost(HUB, FailureDomain.CONTROL_HUB_TO_EXPANSION_HUB)));
        assertEquals(AdvisoryLabel.PROBABLE_EXPANSION_HUB_PATH_FAILURE, report.label());
        assertEquals(EventCorrelator.ISOLATED, report.confidence());
    }

    @Test
    void isolatedElectricalIsProbablePower() {
        AdvisoryReport report = EventCorrelator.evaluate(
                Collections.emptyList(), Collections.singletonList(lost(BATTERY, FailureDomain.ELECTRICAL)));
        assertEquals(AdvisoryLabel.PROBABLE_POWER_DISRUPTION, report.label());
        assertEquals(EventCorrelator.ISOLATED, report.confidence());
    }

    @Test
    void loopOverrunReasonIsProbableLoopOverrun() {
        LinkHealth loop = LinkHealth.builder(LOCALIZATION)
                .domain(FailureDomain.SOFTWARE_LOOP)
                .state(LinkState.LOST)
                .reason(LinkFailureReason.LOOP_OVERRUN)
                .confidence(Confidence.of(0.7))
                .build();
        AdvisoryReport report = EventCorrelator.evaluate(Collections.emptyList(), Collections.singletonList(loop));
        assertEquals(AdvisoryLabel.PROBABLE_LOOP_OVERRUN, report.label());
        assertEquals(EventCorrelator.ISOLATED, report.confidence());
    }

    @Test
    void localizationLossWithoutOverrunIsInsufficient() {
        AdvisoryReport report = EventCorrelator.evaluate(
                Collections.emptyList(),
                Collections.singletonList(lost(LOCALIZATION, FailureDomain.SOFTWARE_LOOP)));
        assertEquals(AdvisoryLabel.INSUFFICIENT_EVIDENCE, report.label());
        assertFalse(report.confidence().isKnown());
    }

    @Test
    void simultaneousCameraAndHubWithoutPowerIsInsufficient() {
        AdvisoryReport report = EventCorrelator.evaluate(
                Collections.emptyList(),
                Arrays.asList(
                        lost(CAMERA, FailureDomain.USB_CAMERA),
                        lost(HUB, FailureDomain.CONTROL_HUB_TO_EXPANSION_HUB)));
        assertEquals(AdvisoryLabel.INSUFFICIENT_EVIDENCE, report.label());
        assertFalse(report.confidence().isKnown());
        assertTrue(report.evidence().size() >= 2);
        assertTrue(report.explanation().toLowerCase().contains("simultaneous"));
    }

    @Test
    void voltageEventWithCompanionFailuresIsLowConfidencePowerNotJamming() {
        BeaconEvent voltage = new BeaconEvent(
                1L, BeaconEventType.AMPER_VOLTAGE, BATTERY, FailureDomain.ELECTRICAL, "cliff");
        AdvisoryReport report = EventCorrelator.evaluate(
                Collections.singletonList(voltage),
                Arrays.asList(
                        lost(CAMERA, FailureDomain.USB_CAMERA),
                        lost(HUB, FailureDomain.CONTROL_HUB_TO_EXPANSION_HUB)));
        assertEquals(AdvisoryLabel.PROBABLE_POWER_DISRUPTION, report.label());
        assertEquals(EventCorrelator.POWER_WITH_COMPANIONS, report.confidence());
        assertTrue(report.explanation().toLowerCase().contains("jamming"));
        assertFalse(report.explanation().toLowerCase().contains("jammed and"));
    }

    @Test
    void driverStationDomainIsInsufficientNotAHeartbeat() {
        AdvisoryReport report = EventCorrelator.evaluate(
                Collections.emptyList(),
                Collections.singletonList(
                        lost(LinkId.of("driverStation"), FailureDomain.DRIVER_STATION_TO_ROBOT_CONTROLLER)));
        assertEquals(AdvisoryLabel.INSUFFICIENT_EVIDENCE, report.label());
        assertFalse(report.confidence().isKnown());
    }

    private static LinkHealth healthy(LinkId id, FailureDomain domain) {
        return LinkHealth.builder(id)
                .domain(domain)
                .state(LinkState.HEALTHY)
                .reason(LinkFailureReason.NONE)
                .confidence(Confidence.of(1.0))
                .build();
    }

    private static LinkHealth lost(LinkId id, FailureDomain domain) {
        return LinkHealth.builder(id)
                .domain(domain)
                .state(LinkState.LOST)
                .reason(LinkFailureReason.EXPLICIT_LOSS_REPORT)
                .confidence(Confidence.of(0.8))
                .build();
    }
}
