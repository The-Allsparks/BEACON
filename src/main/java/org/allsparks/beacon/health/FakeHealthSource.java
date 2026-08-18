package org.allsparks.beacon.health;

import java.util.Objects;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.Freshness;
import org.allsparks.beacon.api.HealthReport;
import org.allsparks.beacon.api.LinkFailureReason;
import org.allsparks.beacon.api.LinkHealth;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.api.LinkState;

/**
 * Manual-report source used before hardware adapters exist. Stores the last
 * observation and overlays {@link FreshnessPolicy} at sample time.
 */
public final class FakeHealthSource implements HealthSource {
    private final LinkId id;
    private final FailureDomain domain;
    private FreshnessPolicy policy;
    private LinkHealth current;

    public FakeHealthSource(LinkId id, FailureDomain domain) {
        this(id, domain, FreshnessPolicy.manualReportsDefault());
    }

    public FakeHealthSource(LinkId id, FailureDomain domain, FreshnessPolicy policy) {
        this.id = Objects.requireNonNull(id, "id");
        this.domain = Objects.requireNonNull(domain, "domain");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.current = LinkHealth.unknown(id, domain);
    }

    @Override
    public LinkId id() {
        return id;
    }

    public FailureDomain domain() {
        return domain;
    }

    public FreshnessPolicy policy() {
        return policy;
    }

    public void setPolicy(FreshnessPolicy policy) {
        this.policy = Objects.requireNonNull(policy, "policy");
    }

    public void setHealth(LinkHealth health) {
        this.current = Objects.requireNonNull(health, "health");
    }

    @Override
    public void accept(HealthReport report) {
        Objects.requireNonNull(report, "report");
        if (!id.equals(report.id())) {
            throw new IllegalArgumentException("Report id " + report.id() + " does not match " + id);
        }
        LinkState state = report.reportedState();
        LinkHealth.Builder builder = LinkHealth.builder(id)
                .domain(report.domain())
                .state(state)
                .reason(report.reason())
                .confidence(report.confidence());
        if (state == LinkState.HEALTHY) {
            builder.lastValidTimestampNanos(report.sourceTimestampNanos())
                    .lastFailureTimestampNanos(current.lastFailureTimestampNanos())
                    .consecutiveSuccesses(current.consecutiveSuccesses() + 1)
                    .consecutiveFailures(0);
        } else {
            builder.lastFailureTimestampNanos(report.sourceTimestampNanos())
                    .lastValidTimestampNanos(current.lastValidTimestampNanos())
                    .consecutiveSuccesses(0)
                    .consecutiveFailures(current.consecutiveFailures() + 1);
        }
        this.current = builder.build();
    }

    @Override
    public LinkHealth sample(long nowNanos) {
        return overlay(current, nowNanos);
    }

    /**
     * Reporter {@link LinkState#LOST} and {@link LinkState#UNKNOWN} stay as stored.
     * Otherwise the snapshot is the worse of the last observation and the age of
     * {@code lastValidTimestampNanos}. Aging does not change consecutive counts.
     */
    LinkHealth overlay(LinkHealth stored, long nowNanos) {
        Objects.requireNonNull(stored, "stored");
        LinkState reporter = stored.state();
        if (reporter == LinkState.LOST || reporter == LinkState.UNKNOWN) {
            return stored;
        }
        long lastValid = stored.lastValidTimestampNanos();
        if (lastValid <= 0L) {
            return stored.toBuilder()
                    .state(LinkState.UNKNOWN)
                    .reason(LinkFailureReason.NEVER_OBSERVED)
                    .build();
        }
        if (nowNanos < lastValid) {
            return stored.toBuilder()
                    .state(LinkState.UNKNOWN)
                    .reason(LinkFailureReason.INSUFFICIENT_EVIDENCE)
                    .build();
        }
        Freshness freshness = policy.classify(lastValid, nowNanos);
        LinkState aged = policy.toLinkState(freshness);
        if (rank(reporter) >= rank(aged)) {
            return stored;
        }
        LinkFailureReason reason = aged == LinkState.STALE
                ? LinkFailureReason.STALE_DATA
                : LinkFailureReason.TIMEOUT;
        return stored.toBuilder().state(aged).reason(reason).build();
    }

    private static int rank(LinkState state) {
        switch (state) {
            case HEALTHY:
                return 0;
            case STALE:
                return 1;
            case LOST:
                return 2;
            case UNKNOWN:
            default:
                return -1;
        }
    }
}
