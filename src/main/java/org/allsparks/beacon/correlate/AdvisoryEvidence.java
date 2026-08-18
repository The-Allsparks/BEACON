package org.allsparks.beacon.correlate;

import java.util.Objects;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.LinkId;

/** One observation supporting an {@link AdvisoryReport}. Not a root cause. */
public final class AdvisoryEvidence {
    private final LinkId linkId;
    private final FailureDomain domain;
    private final String detail;

    public AdvisoryEvidence(LinkId linkId, FailureDomain domain, String detail) {
        this.linkId = Objects.requireNonNull(linkId, "linkId");
        this.domain = domain == null ? FailureDomain.UNKNOWN : domain;
        this.detail = detail == null ? "" : detail;
        if (this.detail.trim().isEmpty()) {
            throw new IllegalArgumentException("Advisory evidence must include a detail");
        }
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
}
