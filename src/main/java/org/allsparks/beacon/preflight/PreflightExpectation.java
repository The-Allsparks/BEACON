package org.allsparks.beacon.preflight;

import java.util.Objects;
import org.allsparks.beacon.api.LinkId;

/** One expected link on the pre-match checklist. */
public final class PreflightExpectation {
    private final LinkId id;
    private final boolean required;

    public PreflightExpectation(LinkId id, boolean required) {
        this.id = Objects.requireNonNull(id, "id");
        this.required = required;
    }

    public static PreflightExpectation required(LinkId id) {
        return new PreflightExpectation(id, true);
    }

    public static PreflightExpectation optional(LinkId id) {
        return new PreflightExpectation(id, false);
    }

    public LinkId id() {
        return id;
    }

    public boolean required() {
        return required;
    }
}
