# Examples

These sketches show integration intent. They are **not** full FTC OpModes (no `hardwareMap` dependency in the library build).

## Phase 0 — observe only

Construct a `BeaconSession` with default flags. Do not enable later phases.

```java
BeaconSession beacon = BeaconSession.create();
// No reports required. Removing this object leaves robot code unchanged.
```

See [phase-0-plan.md](../docs/communications-health/phase-0-plan.md).

## Phase 1 — manually reported passive health

```java
BeaconSession beacon = BeaconSession.create(BeaconFeatureFlags.manualReports());
FakeClock clock = new FakeClock(1L);
beacon.report(HealthReport.healthy(
        LinkId.of("frontCamera"),
        FailureDomain.USB_CAMERA,
        clock.nanoTime(),
        "ViDAR"));
beacon.report(HealthReport.healthy(
        LinkId.of("elevatorEncoder"),
        FailureDomain.SENSOR_BUS,
        clock.nanoTime(),
        "MIMIC"));
beacon.report(HealthReport.healthy(
        LinkId.of("batteryTelemetry"),
        FailureDomain.ELECTRICAL,
        clock.nanoTime(),
        "AMPER"));
beacon.report(HealthReport.healthy(
        LinkId.of("localization"),
        FailureDomain.SOFTWARE_LOOP,
        clock.nanoTime(),
        "Pedro"));
```

Use a positive source timestamp. `0` means never observed and snapshots as `UNKNOWN`, not `HEALTHY`. If you stop reporting, the registry ages the last valid time: delayed still maps to `HEALTHY`, then `STALE`, then `LOST`.

BEACON only stores, ages, and logs. It does not probe or restart devices.

## Later phases

Do not enable from examples until acceptance tests in [phases.md](../docs/communications-health/phases.md) pass and maintainers review. Driver Station safe-stop remains research-only until a supported freshness source is proven.
