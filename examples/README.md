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
beacon.report(HealthReport.healthy(
        LinkId.of("frontCamera"),
        FailureDomain.USB_CAMERA,
        /* sourceTimestampNanos */ 0L,
        "ViDAR"));
beacon.report(HealthReport.healthy(
        LinkId.of("elevatorEncoder"),
        FailureDomain.SENSOR_BUS,
        0L,
        "MIMIC"));
beacon.report(HealthReport.healthy(
        LinkId.of("batteryTelemetry"),
        FailureDomain.ELECTRICAL,
        0L,
        "AMPER"));
beacon.report(HealthReport.healthy(
        LinkId.of("localization"),
        FailureDomain.SOFTWARE_LOOP,
        0L,
        "Pedro"));
```

BEACON only stores and logs. It does not probe or restart devices.

## Later phases

Do not enable from examples until acceptance tests in [phases.md](../docs/communications-health/phases.md) pass and maintainers review. Driver Station safe-stop remains research-only until a supported freshness source is proven.
