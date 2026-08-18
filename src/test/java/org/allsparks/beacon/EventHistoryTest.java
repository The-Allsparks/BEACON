package org.allsparks.beacon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.allsparks.beacon.api.Confidence;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.HealthReport;
import org.allsparks.beacon.api.LinkFailureReason;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.api.LinkState;
import org.allsparks.beacon.clock.FakeClock;
import org.allsparks.beacon.log.BeaconEvent;
import org.allsparks.beacon.log.BeaconEventType;
import org.junit.jupiter.api.Test;

class EventHistoryTest {
    private static final LinkId CAMERA = LinkId.of("frontCamera");

    @Test
    void defaultSessionUsesBoundedLoggerCapacity() {
        BeaconSession session = BeaconSession.create(BeaconFeatureFlags.eventHistory());
        assertEquals(BeaconSession.DEFAULT_LOGGER_CAPACITY, session.logger().capacity());
        assertEquals(256, session.logger().capacity());
        assertFalse(session.isInterventionEnabled());
    }

    @Test
    void phase3OffDoesNotRecordTransitionsOrTiming() {
        FakeClock clock = new FakeClock(1L);
        BeaconSession session = new BeaconSession(BeaconFeatureFlags.defaults(), clock, 16);
        session.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        clock.advanceMillis(81);
        session.observe();
        session.snapshot();
        assertEquals(0, session.logger().size());
        assertFalse(session.isInterventionEnabled());
    }

    @Test
    void firstReportRecordsNoneToCurrentState() {
        FakeClock clock = new FakeClock(1L);
        BeaconSession session = new BeaconSession(BeaconFeatureFlags.eventHistory(), clock, 16);
        session.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        assertEquals(1, count(session, BeaconEventType.HEALTH_TRANSITION));
        assertEquals("NONE->HEALTHY", first(session, BeaconEventType.HEALTH_TRANSITION).detail());
        assertEquals(CAMERA, first(session, BeaconEventType.HEALTH_TRANSITION).linkId());
        assertEquals(0, count(session, BeaconEventType.LOOP_TIMING));
        assertFalse(session.isInterventionEnabled());
    }

    @Test
    void unchangedStateDoesNotRecordAnotherTransition() {
        FakeClock clock = new FakeClock(1L);
        BeaconSession session = new BeaconSession(BeaconFeatureFlags.eventHistory(), clock, 16);
        session.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        clock.advanceMillis(1);
        session.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, clock.nanoTime(), "ViDAR"));
        session.observe();
        assertEquals(1, count(session, BeaconEventType.HEALTH_TRANSITION));
        assertEquals(1, count(session, BeaconEventType.LOOP_TIMING));
    }

    @Test
    void explicitStateChangeRecordsTransition() {
        FakeClock clock = new FakeClock(1L);
        BeaconSession session = new BeaconSession(BeaconFeatureFlags.eventHistory(), clock, 16);
        session.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        session.report(new HealthReport(
                CAMERA,
                FailureDomain.USB_CAMERA,
                LinkState.LOST,
                2L,
                "ViDAR",
                "pipeline stopped",
                LinkFailureReason.EXPLICIT_LOSS_REPORT,
                Confidence.of(0.8)));
        assertEquals(2, count(session, BeaconEventType.HEALTH_TRANSITION));
        assertEquals("HEALTHY->LOST", last(session, BeaconEventType.HEALTH_TRANSITION).detail());
        assertEquals(0, count(session, BeaconEventType.LOOP_TIMING));
    }

    @Test
    void observeRecordsAgingTransitionAndLoopTiming() {
        FakeClock clock = new FakeClock(1L);
        BeaconSession session = new BeaconSession(BeaconFeatureFlags.eventHistory(), clock, 16);
        session.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        clock.advanceMillis(81);
        session.observe();
        assertEquals(2, count(session, BeaconEventType.HEALTH_TRANSITION));
        assertEquals("HEALTHY->STALE", last(session, BeaconEventType.HEALTH_TRANSITION).detail());
        assertEquals(1, count(session, BeaconEventType.LOOP_TIMING));
        assertEquals(LinkId.of("loop"), first(session, BeaconEventType.LOOP_TIMING).linkId());
        assertEquals(FailureDomain.SOFTWARE_LOOP, first(session, BeaconEventType.LOOP_TIMING).domain());
    }

    @Test
    void snapshotDoesNotRecordEvents() {
        FakeClock clock = new FakeClock(1L);
        BeaconSession session = new BeaconSession(BeaconFeatureFlags.eventHistory(), clock, 16);
        session.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        int afterReport = session.logger().size();
        clock.advanceMillis(81);
        session.snapshot();
        assertEquals(afterReport, session.logger().size());
        assertEquals(LinkState.STALE, session.snapshot().get(0).state());
    }

    @Test
    void droppedCountIncrementsWhenHistoryIsFull() {
        FakeClock clock = new FakeClock(1L);
        BeaconSession session = new BeaconSession(
                BeaconFeatureFlags.builder().phase3EventHistory(true).build(), clock, 1);
        session.report(HealthReport.healthy(CAMERA, FailureDomain.USB_CAMERA, 1L, "ViDAR"));
        session.report(new HealthReport(
                CAMERA,
                FailureDomain.USB_CAMERA,
                LinkState.LOST,
                2L,
                "ViDAR",
                "pipeline stopped",
                LinkFailureReason.EXPLICIT_LOSS_REPORT,
                Confidence.of(0.8)));
        assertEquals(1, session.logger().size());
        assertEquals(1, session.logger().capacity());
        assertEquals(1L, session.logger().droppedCount());
        assertTrue(session.logger().exportCsv().contains("HEALTH_TRANSITION"));
        assertFalse(session.flags().isAnyInterventionEnabled());
    }

    private static int count(BeaconSession session, BeaconEventType type) {
        int n = 0;
        for (BeaconEvent event : session.logger().snapshot()) {
            if (event.type() == type) {
                n++;
            }
        }
        return n;
    }

    private static BeaconEvent first(BeaconSession session, BeaconEventType type) {
        for (BeaconEvent event : session.logger().snapshot()) {
            if (event.type() == type) {
                return event;
            }
        }
        throw new AssertionError("missing " + type);
    }

    private static BeaconEvent last(BeaconSession session, BeaconEventType type) {
        BeaconEvent found = null;
        for (BeaconEvent event : session.logger().snapshot()) {
            if (event.type() == type) {
                found = event;
            }
        }
        if (found == null) {
            throw new AssertionError("missing " + type);
        }
        return found;
    }
}
