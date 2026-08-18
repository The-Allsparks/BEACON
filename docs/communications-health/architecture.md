# Architecture

Written before active intervention. Phase 0 implements types and a manual registry only.

Java 11 does not use records in this library. Immutable final classes expose the same accessors as the conceptual record API.

## HealthSource

A component that reports health for one link, device, data stream, or service. It must not perform recovery unless explicitly delegated.

`FakeHealthSource` is the Phase 0 test double. Sibling libraries push `HealthReport` objects; they keep ownership of their devices.

## LinkHealth

Common result:

- `LinkId id`
- `LinkState state` (`UNKNOWN`, `HEALTHY`, `STALE`, `LOST` in Phase 0)
- `FailureDomain domain`
- `lastValidTimestampNanos` / `lastFailureTimestampNanos`
- `consecutiveSuccesses` / `consecutiveFailures`
- `OptionalDouble observedLatencyMs` — empty unless measured
- `LinkFailureReason reason`
- `Confidence confidence` — `unknown()` when evidence is missing

Do not invent unavailable latency or signal measurements.

## HealthRegistry

Collects reports without owning devices. Manual reports from ViDAR, MIMIC, AMPER, Pedro, and robot code are first-class. Removing BEACON means deleting `beacon.report(...)` calls; subsystems do not need rewrites.

`snapshot()` and `get()` call `HealthSource.sample(nowNanos)`. For `FakeHealthSource`, that overlays `FreshnessPolicy` onto the last observation. Consecutive success/failure counts change only when a new report is accepted, not when a stored healthy report ages.

Per-source override: `HealthRegistry.setFreshnessPolicy(id, policy)`. Auto-created sources use `FreshnessPolicy.manualReportsDefault()` (`40 / 80 / 250` ms) unless overridden. That default is for **manual and test reports**, not Driver Station packets.

## FreshnessPolicy

Per-source thresholds for current / delayed / stale / lost. Camera frames, I²C readings, and Driver Station packets do not share timing.

Sampling overlay (Phase 1):

- Reporter `LOST` stays `LOST` (explicit loss wins) until a newer healthy report.
- Reporter `UNKNOWN` stays `UNKNOWN`.
- `lastValidTimestampNanos <= 0` on a non-lost report becomes `UNKNOWN` / `NEVER_OBSERVED`. A timestamp of `0` is not healthy.
- A future timestamp becomes `UNKNOWN` / `INSUFFICIENT_EVIDENCE`.
- Otherwise the snapshot is the worse of reporter state vs age (`HEALTHY` < `STALE` < `LOST`).
- Age past the stale threshold (strictly greater than the inclusive bound) → `STALE` / `STALE_DATA`.
- Age past the lost threshold → `LOST` / `TIMEOUT`.
- `DELAYED` still maps to `LinkState.HEALTHY`. Phase 0 does not expose a delayed snapshot state.

Do not invent latency when overlaying.

## PreflightInspector

Designed for Phase 2. Types exist (`PreflightStatus`, `PreflightFinding`). Every finding must explain why. Results: `READY`, `READY_DEGRADED`, `NOT_READY`, `UNKNOWN`. The inspector must not command actuators.

## EventCorrelator

Phase 3–4. Initially a timeline (`BeaconEventLogger`), not a root-cause engine.

## SafeStateCoordinator

Requests safe-state transitions from registered subsystems. It does not directly command every motor. `SafeStateRequest.shadowOnly` is the Phase 4 default.

## RecoveryPolicy

Specifies whether recovery is allowed, attempt limit, backoff, timeout, and CPU budget. Phase 0 stores the policy; it does not execute it.

## BeaconEventLogger

Bounded rolling buffer. CSV header is TRACE-compatible:

`timestampNanos,type,linkId,domain,detail`

## Command lease

Teleop commands are time-limited. Do not renew a Driver Station lease merely because the OpMode loop continues. See [command-freshness.md](command-freshness.md).

## Feature flags

`BeaconFeatureFlags` defaults: Phase 0 on; all later phases off; `isAnyInterventionEnabled()` is false.

## Loop order (when later phases exist)

1. Sample time (`BeaconClock`)
2. Accept manual reports / source samples
3. Update freshness
4. Record events
5. Preflight only when requested (init)
6. Shadow evaluation
7. **Stop.** Do not write actuators unless a reviewed flag is on.

## Driver Station lifecycle (not implemented)

```text
CONNECTED
    ↓ freshness becomes questionable
SUSPECT
    ↓ verified timeout
SAFE_STOP
    ↓ fresh communication returns
RECOVERY_INHIBIT
    ↓ repeated fresh packets + neutral controls + optional re-arm
CONNECTED
```

Advanced states (`DEGRADED`, `SUSPECT`, `RECOVERING`, `UNSTABLE`, `DISABLED`) are documented for later phases and are **not** on `LinkState` yet.
