package org.allsparks.beacon.lease;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.allsparks.beacon.api.CommandSource;
import org.junit.jupiter.api.Test;

class CommandLeaseAndInhibitTest {
    @Test
    void leaseExpiresIndependentlyOfLoopIteration() {
        CommandLease<Double> lease = new CommandLease<>(1.0, 0L, 100L, CommandSource.DRIVER_STATION_GAMEPAD);
        assertTrue(lease.isFresh(100L));
        assertFalse(lease.isFresh(101L));
    }

    @Test
    void heldForwardStickDoesNotClearInhibit() {
        RecoveryInhibit inhibit = new RecoveryInhibit(3, 50L);
        NeutralControls neutral = NeutralControls.typical();
        inhibit.enter();
        boolean stillInhibited = inhibit.update(10L, true, neutral.isNeutral(0.0, 1.0, 0.0, 0.0, 0.0, 0.0));
        assertTrue(stillInhibited);
        assertTrue(inhibit.isInhibited());
    }

    @Test
    void freshNeutralObservationsReleaseInhibit() {
        RecoveryInhibit inhibit = new RecoveryInhibit(3, 20L);
        NeutralControls neutral = NeutralControls.typical();
        inhibit.enter();
        assertTrue(inhibit.update(0L, true, true));
        assertTrue(inhibit.update(10L, true, true));
        assertFalse(inhibit.update(25L, true, true));
        assertFalse(inhibit.isInhibited());
        assertTrue(neutral.isNeutral(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
    }
}
