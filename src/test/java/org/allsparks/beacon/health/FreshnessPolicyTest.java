package org.allsparks.beacon.health;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.allsparks.beacon.api.Freshness;
import org.allsparks.beacon.api.LinkState;
import org.junit.jupiter.api.Test;

class FreshnessPolicyTest {
    private final FreshnessPolicy policy = FreshnessPolicy.ofMillis(20, 50, 200);

    @Test
    void classifiesAgeWindows() {
        assertEquals(Freshness.UNKNOWN, policy.classify(0L, 1_000_000L));
        assertEquals(Freshness.CURRENT, policy.classify(1L, 10_000_000L));
        assertEquals(Freshness.DELAYED, policy.classify(1L, 30_000_000L));
        assertEquals(Freshness.STALE, policy.classify(1L, 80_000_000L));
        assertEquals(Freshness.LOST, policy.classify(1L, 250_000_000L));
    }

    @Test
    void mapsFreshnessToPhase0States() {
        assertEquals(LinkState.HEALTHY, policy.toLinkState(Freshness.CURRENT));
        assertEquals(LinkState.HEALTHY, policy.toLinkState(Freshness.DELAYED));
        assertEquals(LinkState.STALE, policy.toLinkState(Freshness.STALE));
        assertEquals(LinkState.LOST, policy.toLinkState(Freshness.LOST));
        assertEquals(LinkState.UNKNOWN, policy.toLinkState(Freshness.UNKNOWN));
    }

    @Test
    void futureTimestampIsUnknownNotHealthy() {
        assertEquals(Freshness.UNKNOWN, policy.classify(50L, 10L));
    }
}
