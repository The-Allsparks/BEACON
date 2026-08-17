# AMPER integration

AMPER owns electrical health. BEACON must not re-implement voltage filters or brownout policies.

## Manual report (Phase 1)

```java
beacon.report(HealthReport.healthy(
        LinkId.of("batteryTelemetry"),
        FailureDomain.ELECTRICAL,
        nowNanos,
        "AMPER"));
```

When AMPER later exposes a structured observation, map:

- valid voltage sample → `HEALTHY` in domain `ELECTRICAL`
- invalid/stale sensing → `UNKNOWN` / `STALE`, never invent volts
- sag or brownout suspicion → still `ELECTRICAL`, not `DRIVER_STATION_TO_ROBOT_CONTROLLER`

## Correlation

A Hub “disconnect” that lines up with an AMPER voltage collapse is **probably power**. Treat as advisory until Phase 4 confidence rules exist.

## Recovery inhibit

Do not clear inhibit while AMPER reports unacceptable electrical state, once that handshake is implemented. Phase 0 has no AMPER compile dependency.
