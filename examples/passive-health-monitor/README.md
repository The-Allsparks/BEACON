# Passive health monitor

This example is a desktop sketch of Phase 0–1 behavior. It does not talk to a Control Hub.

```java
package org.allsparks.beacon.examples;

import org.allsparks.beacon.BeaconFeatureFlags;
import org.allsparks.beacon.BeaconSession;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.HealthReport;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.clock.FakeClock;

public final class PassiveHealthMonitorExample {
    public static void main(String[] args) {
        FakeClock clock = new FakeClock();
        BeaconSession beacon = new BeaconSession(BeaconFeatureFlags.manualReports(), clock, 32);
        beacon.report(HealthReport.healthy(
                LinkId.of("frontCamera"), FailureDomain.USB_CAMERA, clock.nanoTime(), "ViDAR"));
        System.out.println("sources=" + beacon.snapshot().size());
        System.out.println("events=" + beacon.logger().size());
        System.out.println("state=" + beacon.snapshot().get(0).state());
        clock.advanceMillis(251);
        System.out.println("aged=" + beacon.snapshot().get(0).state());
        System.out.println("intervention=" + beacon.isInterventionEnabled());
    }
}
```

Copy this into TeamCode only after you understand that BEACON will not stop motors by itself.
