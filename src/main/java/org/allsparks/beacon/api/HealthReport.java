package org.allsparks.beacon.api;

import java.util.Objects;

/**
 * Manual health submission from robot code or a sibling library.
 * BEACON does not probe the named device when this report is stored.
 */
public final class HealthReport {
    private final LinkId id;
    private final FailureDomain domain;
    private final LinkState reportedState;
    private final long sourceTimestampNanos;
    private final String reporter;
    private final String detail;
    private final LinkFailureReason reason;
    private final Confidence confidence;

    public HealthReport(
            LinkId id,
            FailureDomain domain,
            LinkState reportedState,
            long sourceTimestampNanos,
            String reporter,
            String detail,
            LinkFailureReason reason,
            Confidence confidence) {
        this.id = Objects.requireNonNull(id, "id");
        this.domain = Objects.requireNonNull(domain, "domain");
        this.reportedState = Objects.requireNonNull(reportedState, "reportedState");
        this.sourceTimestampNanos = sourceTimestampNanos;
        this.reporter = Objects.requireNonNull(reporter, "reporter");
        this.detail = detail == null ? "" : detail;
        this.reason = Objects.requireNonNull(reason, "reason");
        this.confidence = Objects.requireNonNull(confidence, "confidence");
    }

    public static HealthReport healthy(LinkId id, FailureDomain domain, long timestampNanos, String reporter) {
        return new HealthReport(
                id,
                domain,
                LinkState.HEALTHY,
                timestampNanos,
                reporter,
                "",
                LinkFailureReason.NONE,
                Confidence.of(1.0));
    }

    public LinkId id() {
        return id;
    }

    public FailureDomain domain() {
        return domain;
    }

    public LinkState reportedState() {
        return reportedState;
    }

    public long sourceTimestampNanos() {
        return sourceTimestampNanos;
    }

    public String reporter() {
        return reporter;
    }

    public String detail() {
        return detail;
    }

    public LinkFailureReason reason() {
        return reason;
    }

    public Confidence confidence() {
        return confidence;
    }
}
