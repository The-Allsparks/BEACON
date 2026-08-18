package org.allsparks.beacon;

import java.util.List;
import java.util.Objects;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.HealthReport;
import org.allsparks.beacon.api.LinkHealth;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.clock.BeaconClock;
import org.allsparks.beacon.clock.SystemNanoClock;
import org.allsparks.beacon.health.HealthRegistry;
import org.allsparks.beacon.log.BeaconEvent;
import org.allsparks.beacon.log.BeaconEventLogger;
import org.allsparks.beacon.log.BeaconEventType;
import org.allsparks.beacon.preflight.PreflightExpectation;
import org.allsparks.beacon.preflight.PreflightFinding;
import org.allsparks.beacon.preflight.PreflightInspector;
import org.allsparks.beacon.preflight.PreflightReport;
import org.allsparks.beacon.preflight.PreflightStatus;

/**
 * Per-OpMode BEACON session. Observes and logs; never commands motors, servos,
 * or network hardware.
 */
public final class BeaconSession {
    private final BeaconFeatureFlags flags;
    private final BeaconClock clock;
    private final HealthRegistry registry;
    private final BeaconEventLogger logger;
    private long observeCount;
    private long lastObserveDurationNanos;

    public BeaconSession(BeaconFeatureFlags flags, BeaconClock clock, int loggerCapacity) {
        this.flags = Objects.requireNonNull(flags, "flags");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.registry = new HealthRegistry(clock);
        this.logger = new BeaconEventLogger(loggerCapacity);
    }

    public static BeaconSession create() {
        return new BeaconSession(BeaconFeatureFlags.defaults(), new SystemNanoClock(), 256);
    }

    public static BeaconSession create(BeaconFeatureFlags flags) {
        return new BeaconSession(flags, new SystemNanoClock(), 256);
    }

    /**
     * Accept a manual health report. Safe in Phase 0: the report is stored and
     * logged only when Phase 1 is enabled. Phase 0 still keeps the registry so
     * tests can exercise vocabulary without enabling later phases.
     */
    public LinkHealth report(HealthReport report) {
        Objects.requireNonNull(report, "report");
        long start = clock.nanoTime();
        LinkHealth health = registry.report(report);
        if (flags.isPhase1ManualReports()) {
            logger.record(new BeaconEvent(
                    start,
                    BeaconEventType.MANUAL_REPORT,
                    report.id(),
                    report.domain(),
                    report.reporter() + ": " + report.reportedState()));
        }
        lastObserveDurationNanos = clock.nanoTime() - start;
        observeCount++;
        return health;
    }

    public LinkHealth report(String id, HealthReport report) {
        if (!LinkId.of(id).equals(report.id())) {
            throw new IllegalArgumentException("id mismatch");
        }
        return report(report);
    }

    public List<LinkHealth> snapshot() {
        return registry.snapshot();
    }

    public HealthRegistry registry() {
        return registry;
    }

    public BeaconEventLogger logger() {
        return logger;
    }

    public BeaconFeatureFlags flags() {
        return flags;
    }

    public BeaconClock clock() {
        return clock;
    }

    public long observeCount() {
        return observeCount;
    }

    public long lastObserveDurationNanos() {
        return lastObserveDurationNanos;
    }

    public boolean isInterventionEnabled() {
        return flags.isAnyInterventionEnabled();
    }

    public FailureDomain classifyMissing(LinkId id) {
        if (registry.get(id).isPresent()) {
            return registry.get(id).get().domain();
        }
        return FailureDomain.UNKNOWN;
    }

    /**
     * Evaluate declared expected links. Does not command actuators. When Phase 2
     * is disabled, the result is {@link PreflightStatus#UNKNOWN} rather than a
     * fabricated ready or not-ready call.
     */
    public PreflightReport preflight(List<PreflightExpectation> expected) {
        Objects.requireNonNull(expected, "expected");
        if (!flags.isPhase2Preflight()) {
            PreflightFinding finding = new PreflightFinding(
                    LinkId.of("preflight"),
                    true,
                    PreflightStatus.UNKNOWN,
                    "Phase 2 preflight is disabled by feature flags.");
            return PreflightReport.of(PreflightStatus.UNKNOWN, java.util.Collections.singletonList(finding));
        }
        PreflightReport report = PreflightInspector.evaluate(registry, expected);
        logger.record(new BeaconEvent(
                clock.nanoTime(),
                BeaconEventType.PREFLIGHT,
                LinkId.of("preflight"),
                FailureDomain.SOFTWARE_LOOP,
                report.status().name()));
        return report;
    }
}
