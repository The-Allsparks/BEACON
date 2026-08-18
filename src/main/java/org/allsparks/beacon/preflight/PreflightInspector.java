package org.allsparks.beacon.preflight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.allsparks.beacon.api.LinkHealth;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.api.LinkState;
import org.allsparks.beacon.health.HealthRegistry;

/**
 * Evaluates declared expected links against the health registry. Does not probe
 * devices, invent Driver Station heartbeats, or command actuators.
 */
public final class PreflightInspector {
    private static final LinkId PREFLIGHT_ID = LinkId.of("preflight");

    private PreflightInspector() {}

    public static PreflightReport evaluate(HealthRegistry registry, List<PreflightExpectation> expected) {
        Objects.requireNonNull(registry, "registry");
        Objects.requireNonNull(expected, "expected");
        if (expected.isEmpty()) {
            PreflightFinding finding = new PreflightFinding(
                    PREFLIGHT_ID,
                    true,
                    PreflightStatus.UNKNOWN,
                    "Preflight was requested with no expected links. Declare required and optional links before a match.");
            return PreflightReport.of(PreflightStatus.UNKNOWN, Collections.singletonList(finding));
        }
        List<PreflightFinding> findings = new ArrayList<>(expected.size());
        PreflightStatus worst = PreflightStatus.READY;
        for (PreflightExpectation expectation : expected) {
            Objects.requireNonNull(expectation, "expectation");
            PreflightFinding finding = evaluateOne(registry, expectation);
            findings.add(finding);
            worst = worse(worst, finding.status());
        }
        return PreflightReport.of(worst, findings);
    }

    private static PreflightFinding evaluateOne(HealthRegistry registry, PreflightExpectation expectation) {
        Optional<LinkHealth> observed = registry.get(expectation.id());
        if (!observed.isPresent()) {
            if (expectation.required()) {
                return new PreflightFinding(
                        expectation.id(),
                        true,
                        PreflightStatus.UNKNOWN,
                        "No health report for required link "
                                + expectation.id()
                                + ". Evidence is missing; this is not treated as a Driver Station network failure.");
            }
            return new PreflightFinding(
                    expectation.id(),
                    false,
                    PreflightStatus.READY_DEGRADED,
                    "Optional link " + expectation.id() + " has no health report.");
        }
        LinkHealth health = observed.get();
        LinkState state = health.state();
        if (state == LinkState.HEALTHY) {
            return new PreflightFinding(
                    expectation.id(),
                    expectation.required(),
                    PreflightStatus.READY,
                    "Link " + expectation.id() + " is healthy.");
        }
        if (state == LinkState.STALE) {
            if (expectation.required()) {
                return new PreflightFinding(
                        expectation.id(),
                        true,
                        PreflightStatus.NOT_READY,
                        "Required link "
                                + expectation.id()
                                + " is stale ("
                                + health.reason()
                                + ").");
            }
            return new PreflightFinding(
                    expectation.id(),
                    false,
                    PreflightStatus.READY_DEGRADED,
                    "Optional link " + expectation.id() + " is stale (" + health.reason() + ").");
        }
        if (state == LinkState.LOST) {
            if (expectation.required()) {
                return new PreflightFinding(
                        expectation.id(),
                        true,
                        PreflightStatus.NOT_READY,
                        "Required link " + expectation.id() + " is lost (" + health.reason() + ").");
            }
            return new PreflightFinding(
                    expectation.id(),
                    false,
                    PreflightStatus.READY_DEGRADED,
                    "Optional link " + expectation.id() + " is lost (" + health.reason() + ").");
        }
        if (expectation.required()) {
            return new PreflightFinding(
                    expectation.id(),
                    true,
                    PreflightStatus.UNKNOWN,
                    "Required link "
                            + expectation.id()
                            + " has unknown health ("
                            + health.reason()
                            + ").");
        }
        return new PreflightFinding(
                expectation.id(),
                false,
                PreflightStatus.READY_DEGRADED,
                "Optional link " + expectation.id() + " has unknown health (" + health.reason() + ").");
    }

    static PreflightStatus worse(PreflightStatus left, PreflightStatus right) {
        return rank(left) >= rank(right) ? left : right;
    }

    private static int rank(PreflightStatus status) {
        switch (status) {
            case NOT_READY:
                return 3;
            case UNKNOWN:
                return 2;
            case READY_DEGRADED:
                return 1;
            case READY:
            default:
                return 0;
        }
    }
}
