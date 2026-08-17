package org.allsparks.beacon.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.OptionalDouble;
import org.junit.jupiter.api.Test;

class LinkHealthTest {
    @Test
    void unknownUsesExplicitAbsence() {
        LinkHealth health = LinkHealth.unknown(LinkId.of("frontCamera"), FailureDomain.USB_CAMERA);
        assertEquals(LinkState.UNKNOWN, health.state());
        assertEquals(LinkFailureReason.NEVER_OBSERVED, health.reason());
        assertFalse(health.observedLatencyMs().isPresent());
        assertFalse(health.confidence().isKnown());
    }

    @Test
    void builderRejectsNegativeCounts() {
        assertThrows(IllegalArgumentException.class, () -> LinkHealth.builder(LinkId.of("hub"))
                .consecutiveFailures(-1)
                .build());
    }

    @Test
    void latencyMayBeRecordedWhenMeasured() {
        LinkHealth health = LinkHealth.builder(LinkId.of("expansionHub"))
                .domain(FailureDomain.CONTROL_HUB_TO_EXPANSION_HUB)
                .state(LinkState.HEALTHY)
                .reason(LinkFailureReason.NONE)
                .confidence(Confidence.of(0.9))
                .observedLatencyMs(4.5)
                .consecutiveSuccesses(3)
                .lastValidTimestampNanos(100L)
                .build();
        assertEquals(OptionalDouble.of(4.5), health.observedLatencyMs());
        assertEquals(3, health.consecutiveSuccesses());
    }

    @Test
    void confidenceUnknownIsNotZero() {
        Confidence unknown = Confidence.unknown();
        assertFalse(unknown.isKnown());
        assertTrue(Double.isNaN(unknown.valueOrNaN()));
        assertThrows(IllegalArgumentException.class, () -> Confidence.of(1.2));
    }
}
