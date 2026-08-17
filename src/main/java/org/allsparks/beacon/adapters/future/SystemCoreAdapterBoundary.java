package org.allsparks.beacon.adapters.future;

/**
 * Boundary for a future SystemCore adapter. No SystemCore APIs are called.
 *
 * <p>Authoritative SystemCore communication documentation was not available
 * when this scaffold was written. Do not treat this class as evidence that
 * SystemCore exposes Driver Station freshness, hub health, or recovery hooks.
 */
public final class SystemCoreAdapterBoundary {
    private SystemCoreAdapterBoundary() {}

    public static boolean isAvailable() {
        return false;
    }

    public static String status() {
        return "UNAVAILABLE: wait for authoritative SystemCore documentation";
    }
}
