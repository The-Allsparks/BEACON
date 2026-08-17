package org.allsparks.beacon.log;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.LinkId;
import org.junit.jupiter.api.Test;

class BeaconEventLoggerTest {
    @Test
    void dropsOldestWhenFull() {
        BeaconEventLogger logger = new BeaconEventLogger(2);
        logger.record(event(1L, "a"));
        logger.record(event(2L, "b"));
        logger.record(event(3L, "c"));
        assertEquals(2, logger.size());
        assertEquals(1L, logger.droppedCount());
        assertEquals("c", logger.snapshot().get(1).detail());
        assertTrue(logger.exportCsv().startsWith("timestampNanos,type,linkId,domain,detail"));
    }

    private static BeaconEvent event(long timestamp, String detail) {
        return new BeaconEvent(
                timestamp,
                BeaconEventType.MANUAL_REPORT,
                LinkId.of("frontCamera"),
                FailureDomain.USB_CAMERA,
                detail);
    }
}
