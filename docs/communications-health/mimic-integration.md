# MIMIC integration

MIMIC owns mechanism lifecycle and mechanism-specific safe states. BEACON requests; MIMIC decides.

## Manual report (Phase 1)

```java
beacon.report(HealthReport.healthy(
        LinkId.of("elevatorEncoder"),
        FailureDomain.SENSOR_BUS,
        nowNanos,
        "MIMIC"));
```

## Communication-safe contract (Phase 6)

Each MIMIC mechanism declares what “safe” means on verified comms loss:

- gravity loads keep holding / counterbalance / valid ratchet conditions;
- noncritical rollers stop;
- interrupted profiles do not auto-complete on reconnect.

BEACON must not suddenly zero a lift that needs holding current.

## No compile dependency

Phase 0 does not import MIMIC. Reports are strings and `HealthReport` objects.
