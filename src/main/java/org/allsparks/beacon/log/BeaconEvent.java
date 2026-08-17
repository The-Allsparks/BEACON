package org.allsparks.beacon.log;

import java.util.Objects;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.LinkId;

/** Immutable log record. Format is intentionally TRACE-compatible. */
public final class BeaconEvent {
    private final long timestampNanos;
    private final BeaconEventType type;
    private final LinkId linkId;
    private final FailureDomain domain;
    private final String detail;

    public BeaconEvent(
            long timestampNanos,
            BeaconEventType type,
            LinkId linkId,
            FailureDomain domain,
            String detail) {
        this.timestampNanos = timestampNanos;
        this.type = Objects.requireNonNull(type, "type");
        this.linkId = linkId;
        this.domain = domain == null ? FailureDomain.UNKNOWN : domain;
        this.detail = detail == null ? "" : detail;
    }

    public long timestampNanos() {
        return timestampNanos;
    }

    public BeaconEventType type() {
        return type;
    }

    public LinkId linkId() {
        return linkId;
    }

    public FailureDomain domain() {
        return domain;
    }

    public String detail() {
        return detail;
    }

    public String toCsvRow() {
        String id = linkId == null ? "" : linkId.value();
        return timestampNanos + "," + type + "," + id + "," + domain + "," + escape(detail);
    }

    private static String escape(String value) {
        if (value.indexOf(',') < 0 && value.indexOf('"') < 0) {
            return value;
        }
        return '"' + value.replace("\"", "\"\"") + '"';
    }
}
