package org.allsparks.beacon.api;

import java.util.Objects;

/**
 * Stable identifier for one communication path, device, data stream, or service.
 * Values are team-chosen strings such as {@code frontCamera} or {@code expansionHub}.
 */
public final class LinkId {
    private final String value;

    private LinkId(String value) {
        this.value = value;
    }

    public static LinkId of(String value) {
        Objects.requireNonNull(value, "value");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("LinkId must be non-empty");
        }
        return new LinkId(trimmed);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LinkId)) {
            return false;
        }
        return value.equals(((LinkId) other).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
