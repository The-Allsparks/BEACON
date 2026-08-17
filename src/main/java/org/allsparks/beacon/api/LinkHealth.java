package org.allsparks.beacon.api;

import java.util.Objects;
import java.util.OptionalDouble;

/**
 * Immutable health snapshot for one link. Java 11 compatible equivalent of the
 * conceptual record API. Unavailable latency is {@link OptionalDouble#empty()},
 * never a fabricated number.
 */
public final class LinkHealth {
    private final LinkId id;
    private final LinkState state;
    private final FailureDomain domain;
    private final long lastValidTimestampNanos;
    private final long lastFailureTimestampNanos;
    private final int consecutiveSuccesses;
    private final int consecutiveFailures;
    private final OptionalDouble observedLatencyMs;
    private final LinkFailureReason reason;
    private final Confidence confidence;

    private LinkHealth(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id");
        this.state = Objects.requireNonNull(builder.state, "state");
        this.domain = Objects.requireNonNull(builder.domain, "domain");
        this.lastValidTimestampNanos = builder.lastValidTimestampNanos;
        this.lastFailureTimestampNanos = builder.lastFailureTimestampNanos;
        this.consecutiveSuccesses = builder.consecutiveSuccesses;
        this.consecutiveFailures = builder.consecutiveFailures;
        this.observedLatencyMs = builder.observedLatencyMs;
        this.reason = Objects.requireNonNull(builder.reason, "reason");
        this.confidence = Objects.requireNonNull(builder.confidence, "confidence");
        if (consecutiveSuccesses < 0 || consecutiveFailures < 0) {
            throw new IllegalArgumentException("Consecutive counts must be >= 0");
        }
    }

    public static Builder builder(LinkId id) {
        return new Builder(id);
    }

    public static LinkHealth unknown(LinkId id, FailureDomain domain) {
        return builder(id)
                .domain(domain)
                .state(LinkState.UNKNOWN)
                .reason(LinkFailureReason.NEVER_OBSERVED)
                .confidence(Confidence.unknown())
                .build();
    }

    public LinkId id() {
        return id;
    }

    public LinkState state() {
        return state;
    }

    public FailureDomain domain() {
        return domain;
    }

    public long lastValidTimestampNanos() {
        return lastValidTimestampNanos;
    }

    public long lastFailureTimestampNanos() {
        return lastFailureTimestampNanos;
    }

    public int consecutiveSuccesses() {
        return consecutiveSuccesses;
    }

    public int consecutiveFailures() {
        return consecutiveFailures;
    }

    public OptionalDouble observedLatencyMs() {
        return observedLatencyMs;
    }

    public LinkFailureReason reason() {
        return reason;
    }

    public Confidence confidence() {
        return confidence;
    }

    public static final class Builder {
        private final LinkId id;
        private LinkState state = LinkState.UNKNOWN;
        private FailureDomain domain = FailureDomain.UNKNOWN;
        private long lastValidTimestampNanos;
        private long lastFailureTimestampNanos;
        private int consecutiveSuccesses;
        private int consecutiveFailures;
        private OptionalDouble observedLatencyMs = OptionalDouble.empty();
        private LinkFailureReason reason = LinkFailureReason.NEVER_OBSERVED;
        private Confidence confidence = Confidence.unknown();

        private Builder(LinkId id) {
            this.id = Objects.requireNonNull(id, "id");
        }

        public Builder state(LinkState value) {
            this.state = value;
            return this;
        }

        public Builder domain(FailureDomain value) {
            this.domain = value;
            return this;
        }

        public Builder lastValidTimestampNanos(long value) {
            this.lastValidTimestampNanos = value;
            return this;
        }

        public Builder lastFailureTimestampNanos(long value) {
            this.lastFailureTimestampNanos = value;
            return this;
        }

        public Builder consecutiveSuccesses(int value) {
            this.consecutiveSuccesses = value;
            return this;
        }

        public Builder consecutiveFailures(int value) {
            this.consecutiveFailures = value;
            return this;
        }

        public Builder observedLatencyMs(OptionalDouble value) {
            this.observedLatencyMs = Objects.requireNonNull(value, "observedLatencyMs");
            return this;
        }

        public Builder observedLatencyMs(double value) {
            this.observedLatencyMs = OptionalDouble.of(value);
            return this;
        }

        public Builder reason(LinkFailureReason value) {
            this.reason = value;
            return this;
        }

        public Builder confidence(Confidence value) {
            this.confidence = value;
            return this;
        }

        public LinkHealth build() {
            return new LinkHealth(this);
        }
    }
}
