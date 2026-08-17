package org.allsparks.beacon.api;

/**
 * Confidence that a classification or health state is supported by evidence.
 *
 * <p>Values are in {@code [0.0, 1.0]}. Missing evidence must use {@link #unknown()}
 * rather than inventing a high-confidence diagnosis.
 */
public final class Confidence {
    private final double value;
    private final boolean known;

    private Confidence(double value, boolean known) {
        this.value = value;
        this.known = known;
    }

    public static Confidence of(double value) {
        if (Double.isNaN(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("Confidence must be in [0, 1], got " + value);
        }
        return new Confidence(value, true);
    }

    /** Explicit absence: callers must not treat this as 0.0 or 1.0. */
    public static Confidence unknown() {
        return new Confidence(Double.NaN, false);
    }

    public boolean isKnown() {
        return known;
    }

    public double valueOrNaN() {
        return known ? value : Double.NaN;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Confidence)) {
            return false;
        }
        Confidence that = (Confidence) other;
        if (known != that.known) {
            return false;
        }
        return !known || Double.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return known ? Double.hashCode(value) : 0;
    }

    @Override
    public String toString() {
        return known ? String.valueOf(value) : "UNKNOWN";
    }
}
