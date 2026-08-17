package org.allsparks.beacon.coord;

import java.util.Objects;
import org.allsparks.beacon.api.LinkId;

/** Request that a registered subsystem enter its own safe state. Does not command motors. */
public final class SafeStateRequest {
    private final LinkId subsystem;
    private final String reason;
    private final boolean shadowOnly;

    public SafeStateRequest(LinkId subsystem, String reason, boolean shadowOnly) {
        this.subsystem = Objects.requireNonNull(subsystem, "subsystem");
        this.reason = Objects.requireNonNull(reason, "reason");
        this.shadowOnly = shadowOnly;
        if (reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Safe-state requests must include a reason");
        }
    }

    public LinkId subsystem() {
        return subsystem;
    }

    public String reason() {
        return reason;
    }

    public boolean shadowOnly() {
        return shadowOnly;
    }
}
