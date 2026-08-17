package org.allsparks.beacon.clock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FakeClockTest {
    @Test
    void advancesWithoutWallTime() {
        FakeClock clock = new FakeClock(5L);
        clock.advanceMillis(2);
        assertEquals(2_000_005L, clock.nanoTime());
        clock.setNanos(9L);
        assertEquals(9L, clock.nanoTime());
    }
}
