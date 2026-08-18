package org.allsparks.beacon.correlate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.allsparks.beacon.api.Confidence;

/**
 * One advisory classification. Always includes a label, a confidence (possibly
 * {@link Confidence#unknown()}), an evidence list, and an explanation.
 * Does not command actuators.
 */
public final class AdvisoryReport {
    private final AdvisoryLabel label;
    private final Confidence confidence;
    private final List<AdvisoryEvidence> evidence;
    private final String explanation;

    public AdvisoryReport(
            AdvisoryLabel label,
            Confidence confidence,
            List<AdvisoryEvidence> evidence,
            String explanation) {
        this.label = Objects.requireNonNull(label, "label");
        this.confidence = Objects.requireNonNull(confidence, "confidence");
        Objects.requireNonNull(evidence, "evidence");
        this.evidence = Collections.unmodifiableList(new ArrayList<>(evidence));
        this.explanation = Objects.requireNonNull(explanation, "explanation");
        if (this.explanation.trim().isEmpty()) {
            throw new IllegalArgumentException("Advisory reports must include an explanation");
        }
        if (this.evidence.isEmpty()) {
            throw new IllegalArgumentException("Advisory reports must include evidence");
        }
        for (AdvisoryEvidence item : this.evidence) {
            Objects.requireNonNull(item, "evidence item");
        }
    }

    public static AdvisoryReport of(
            AdvisoryLabel label,
            Confidence confidence,
            List<AdvisoryEvidence> evidence,
            String explanation) {
        return new AdvisoryReport(label, confidence, evidence, explanation);
    }

    public AdvisoryLabel label() {
        return label;
    }

    public Confidence confidence() {
        return confidence;
    }

    public List<AdvisoryEvidence> evidence() {
        return evidence;
    }

    public String explanation() {
        return explanation;
    }
}
