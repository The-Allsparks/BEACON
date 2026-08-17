package org.allsparks.beacon.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.HealthReport;
import org.allsparks.beacon.api.LinkHealth;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.api.LinkState;
import org.allsparks.beacon.clock.FakeClock;
import org.junit.jupiter.api.Test;

class HealthRegistryTest {
    @Test
    void storesManualReportsWithoutProbing() {
        FakeClock clock = new FakeClock(1_000L);
        HealthRegistry registry = new HealthRegistry(clock);
        LinkId camera = LinkId.of("frontCamera");
        LinkHealth health = registry.report(HealthReport.healthy(camera, FailureDomain.USB_CAMERA, 1_000L, "ViDAR"));
        assertEquals(LinkState.HEALTHY, health.state());
        assertEquals(1, registry.size());
        assertTrue(registry.lastReport(camera).isPresent());
        assertEquals("ViDAR", registry.lastReport(camera).get().reporter());
    }

    @Test
    void fakeSourceAcceptsExplicitLoss() {
        FakeHealthSource source = new FakeHealthSource(LinkId.of("elevatorEncoder"), FailureDomain.SENSOR_BUS);
        source.accept(new HealthReport(
                LinkId.of("elevatorEncoder"),
                FailureDomain.SENSOR_BUS,
                LinkState.LOST,
                5L,
                "MIMIC",
                "no new ticks",
                org.allsparks.beacon.api.LinkFailureReason.EXPLICIT_LOSS_REPORT,
                org.allsparks.beacon.api.Confidence.of(0.8)));
        assertEquals(LinkState.LOST, source.sample(10L).state());
        assertEquals(1, source.sample(10L).consecutiveFailures());
    }
}
