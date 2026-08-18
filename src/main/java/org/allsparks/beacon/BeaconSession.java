package org.allsparks.beacon;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.allsparks.beacon.api.Confidence;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.HealthReport;
import org.allsparks.beacon.api.LinkHealth;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.api.LinkState;
import org.allsparks.beacon.clock.BeaconClock;
import org.allsparks.beacon.clock.SystemNanoClock;
import org.allsparks.beacon.correlate.AdvisoryEvidence;
import org.allsparks.beacon.correlate.AdvisoryLabel;
import org.allsparks.beacon.correlate.AdvisoryReport;
import org.allsparks.beacon.correlate.EventCorrelator;
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
    /** Default in-memory history bound used by {@link #create()} factories. */
    public static final int DEFAULT_LOGGER_CAPACITY = 256;

    private final BeaconFeatureFlags flags;
    private final BeaconClock clock;
    private final HealthRegistry registry;
    private final BeaconEventLogger logger;
    private final Map<LinkId, LinkState> lastLoggedStates = new HashMap<>();
    private long observeCount;
    private long lastObserveDurationNanos;

    public BeaconSession(BeaconFeatureFlags flags, BeaconClock clock, int loggerCapacity) {
        this.flags = Objects.requireNonNull(flags, "flags");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.registry = new HealthRegistry(clock);
        this.logger = new BeaconEventLogger(loggerCapacity);
    }

    /**
     * Create a session with default flags and a 256-event in-memory logger.
     * That capacity is the software bound for Phase 3 history; it is not a
     * Control Hub overhead measurement.
     */
    public static BeaconSession create() {
        return new BeaconSession(BeaconFeatureFlags.defaults(), new SystemNanoClock(), DEFAULT_LOGGER_CAPACITY);
    }

    public static BeaconSession create(BeaconFeatureFlags flags) {
        return new BeaconSession(flags, new SystemNanoClock(), DEFAULT_LOGGER_CAPACITY);
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
        if (flags.isPhase3EventHistory()) {
            recordHealthTransition(health, start);
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

    /**
     * Classify current snapshot and event history into one advisory label.
     * Does not command actuators and does not log a shadow safe-stop (Phase 4
     * shadow remains issue #18). When Phase 4 is disabled, the result is
     * {@link AdvisoryLabel#INSUFFICIENT_EVIDENCE} rather than a fabricated cause.
     */
    public AdvisoryReport advise() {
        if (!flags.isPhase4AdvisoryShadow()) {
            AdvisoryEvidence evidence = new AdvisoryEvidence(
                    LinkId.of("correlator"),
                    FailureDomain.UNKNOWN,
                    "Phase 4 advisory correlation is disabled by feature flags.");
            return AdvisoryReport.of(
                    AdvisoryLabel.INSUFFICIENT_EVIDENCE,
                    Confidence.unknown(),
                    Collections.singletonList(evidence),
                    "Phase 4 advisory correlation is disabled by feature flags.");
        }
        AdvisoryReport report = EventCorrelator.evaluate(logger.snapshot(), registry.snapshot());
        logger.record(new BeaconEvent(
                clock.nanoTime(),
                BeaconEventType.FAILURE_DOMAIN_HINT,
                LinkId.of("correlator"),
                FailureDomain.UNKNOWN,
                report.label().name()));
        return report;
    }

    /**
     * Sample all registered links. When Phase 3 is enabled, records health
     * transitions discovered by freshness aging and one loop-timing event.
     * Does not command actuators. Logging is bounded by logger capacity.
     */
    public List<LinkHealth> observe() {
        long start = clock.nanoTime();
        List<LinkHealth> snap = registry.snapshot();
        if (flags.isPhase3EventHistory()) {
            for (LinkHealth health : snap) {
                recordHealthTransition(health, start);
            }
        }
        lastObserveDurationNanos = clock.nanoTime() - start;
        observeCount++;
        if (flags.isPhase3EventHistory()) {
            logger.record(new BeaconEvent(
                    start,
                    BeaconEventType.LOOP_TIMING,
                    LinkId.of("loop"),
                    FailureDomain.SOFTWARE_LOOP,
                    Long.toString(lastObserveDurationNanos)));
        }
        return snap;
    }

    private void recordHealthTransition(LinkHealth health, long timestampNanos) {
        LinkState previous = lastLoggedStates.put(health.id(), health.state());
        if (previous == health.state()) {
            return;
        }
        String from = previous == null ? "NONE" : previous.name();
        logger.record(new BeaconEvent(
                timestampNanos,
                BeaconEventType.HEALTH_TRANSITION,
                health.id(),
                health.domain(),
                from + "->" + health.state().name()));
    }
}
