package org.allsparks.beacon.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.allsparks.beacon.api.Freshness;
import org.allsparks.beacon.clock.FakeClock;
import org.junit.jupiter.api.Test;

/** Simulation-style timing cases with no hardware and no output changes. */
class FreshnessSimulationTest {
    @Test
    void ordinaryJitterStaysCurrent() {
        FreshnessPolicy policy = FreshnessPolicy.ofMillis(40, 80, 250);
        FakeClock clock = new FakeClock(1L);
        long lastValid = 1L;
        for (int i = 0; i < 20; i++) {
            clock.advanceMillis(15);
            assertEquals(Freshness.CURRENT, policy.classify(lastValid, clock.nanoTime()));
            lastValid = clock.nanoTime();
        }
    }

    @Test
    void completeLossCrossesLostThreshold() {
        FreshnessPolicy policy = FreshnessPolicy.ofMillis(40, 80, 250);
        assertEquals(Freshness.LOST, policy.classify(1L, 400_000_001L));
    }

    @Test
    void heldCommandIsNotAFreshnessSignal() {
        FreshnessPolicy policy = FreshnessPolicy.ofMillis(40, 80, 250);
        long lastPacket = 1L;
        long loopNow = 300_000_001L;
        assertEquals(Freshness.LOST, policy.classify(lastPacket, loopNow));
        assertNotEquals(Freshness.CURRENT, policy.classify(lastPacket, loopNow));
    }
}
