package org.allsparks.beacon.health;

import java.util.Objects;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.HealthReport;
import org.allsparks.beacon.api.LinkHealth;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.api.LinkState;

/** Test double that returns a caller-assigned {@link LinkHealth}. */
public final class FakeHealthSource implements HealthSource {
    private final LinkId id;
    private final FailureDomain domain;
    private LinkHealth current;

    public FakeHealthSource(LinkId id, FailureDomain domain) {
        this.id = Objects.requireNonNull(id, "id");
        this.domain = Objects.requireNonNull(domain, "domain");
        this.current = LinkHealth.unknown(id, domain);
    }

    @Override
    public LinkId id() {
        return id;
    }

    public FailureDomain domain() {
        return domain;
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
            builder.lastValidTimestampNanos(report.sourceTimestampNanos()).consecutiveSuccesses(1);
        } else {
            builder.lastFailureTimestampNanos(report.sourceTimestampNanos()).consecutiveFailures(1);
        }
        this.current = builder.build();
    }

    @Override
    public LinkHealth sample(long nowNanos) {
        return current;
    }
}
