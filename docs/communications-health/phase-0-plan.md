# Phase 0 file-level plan

## Implemented in this scaffold

| Path | Role |
|------|------|
| `src/main/java/org/allsparks/beacon/BeaconPhase.java` | Phase enum |
| `src/main/java/org/allsparks/beacon/BeaconFeatureFlags.java` | Flags; intervention default off |
| `src/main/java/org/allsparks/beacon/BeaconSession.java` | Observe/report only |
| `src/main/java/org/allsparks/beacon/api/*` | LinkId, LinkState, LinkHealth, HealthReport, domains, reasons, freshness, confidence, command source |
| `src/main/java/org/allsparks/beacon/clock/*` | BeaconClock, SystemNanoClock, FakeClock |
| `src/main/java/org/allsparks/beacon/health/*` | HealthSource, FakeHealthSource, HealthRegistry, FreshnessPolicy |
| `src/main/java/org/allsparks/beacon/lease/*` | CommandLease, RecoveryInhibit, NeutralControls |
| `src/main/java/org/allsparks/beacon/log/*` | Bounded TRACE-compatible logger |
| `src/main/java/org/allsparks/beacon/preflight/*` | Status and finding types |
| `src/main/java/org/allsparks/beacon/coord/SafeStateRequest.java` | Request type; no dispatch |
| `src/main/java/org/allsparks/beacon/recovery/RecoveryPolicy.java` | Policy type; no execution |
| `src/main/java/org/allsparks/beacon/adapters/future/SystemCoreAdapterBoundary.java` | Explicit unavailable |
| `src/test/java/org/allsparks/beacon/**` | Unit, simulation-style, doc-link tests |
| `docs/communications-health/**` | Research, feasibility, architecture, phases |
| `examples/passive-health-monitor/` | Desktop sketch |

## Intentionally absent

- Motor or servo writes
- Hardware restart
- Official network modification
- Private SDK usage in production code
- Production DS loss detector
- Automatic adapters for ViDAR/MIMIC/AMPER/Pedro (manual reports only)

## Next files (Phase 1, after review)

- Optional telemetry formatter (string only, official DS telemetry)
- Overhead histogram helper
- Example TeamCode snippet in `examples/` (still no SDK dependency)

Stop before Phase 5 implementation until a supported freshness source exists.
