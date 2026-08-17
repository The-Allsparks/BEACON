package org.allsparks.beacon.preflight;

import java.util.Objects;
import org.allsparks.beacon.api.LinkId;

/** One checklist item. Phase 0 stores findings; it does not command actuators. */
public final class PreflightFinding {
    private final LinkId id;
    private final boolean required;
    private final PreflightStatus status;
    private final String explanation;

    public PreflightFinding(LinkId id, boolean required, PreflightStatus status, String explanation) {
        this.id = Objects.requireNonNull(id, "id");
        this.required = required;
        this.status = Objects.requireNonNull(status, "status");
        this.explanation = Objects.requireNonNull(explanation, "explanation");
        if (explanation.trim().isEmpty()) {
            throw new IllegalArgumentException("Preflight findings must explain why");
        }
    }

    public LinkId id() {
        return id;
    }

    public boolean required() {
        return required;
    }

    public PreflightStatus status() {
        return status;
    }

    public String explanation() {
        return explanation;
    }
}
