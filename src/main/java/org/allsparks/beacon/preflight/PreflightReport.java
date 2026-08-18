package org.allsparks.beacon.preflight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Aggregate preflight result. Does not command actuators. */
public final class PreflightReport {
    private final PreflightStatus status;
    private final List<PreflightFinding> findings;

    public PreflightReport(PreflightStatus status, List<PreflightFinding> findings) {
        this.status = Objects.requireNonNull(status, "status");
        Objects.requireNonNull(findings, "findings");
        this.findings = Collections.unmodifiableList(new ArrayList<>(findings));
        if (this.findings.isEmpty() && status != PreflightStatus.READY) {
            throw new IllegalArgumentException("Non-ready preflight reports must include findings");
        }
        for (PreflightFinding finding : this.findings) {
            Objects.requireNonNull(finding, "finding");
        }
    }

    public static PreflightReport of(PreflightStatus status, List<PreflightFinding> findings) {
        return new PreflightReport(status, findings);
    }

    public PreflightStatus status() {
        return status;
    }

    public List<PreflightFinding> findings() {
        return findings;
    }
}
