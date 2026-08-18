# Preflight

Phase 2. `PreflightInspector` evaluates **declared** expected links against the health registry. It does not probe hardware or command actuators. `BeaconSession.preflight` is disabled unless `BeaconFeatureFlags.preflight()` (or `phase2Preflight(true)`) is set.

## Purpose

Find problems **before** the match. The checklist must not command actuators.

## Possible results

| Status | Meaning |
|--------|---------|
| `READY` | All required items have explanations that support go |
| `READY_DEGRADED` | Optional item failed or is absent; robot may play with documented limits |
| `NOT_READY` | A required item failed (`STALE` or `LOST`) |
| `UNKNOWN` | Evidence missing; fail closed for required items |

Every finding must include a non-empty explanation.

## Evaluation rules

- Teams pass a list of `PreflightExpectation.required` / `optional` link ids. BEACON does not invent a robot-wide default checklist.
- Optional absence → `READY_DEGRADED`.
- Required link with **no report** → `UNKNOWN`, not a fabricated Driver Station `NOT_READY`.
- Required `STALE` or `LOST` → `NOT_READY`.
- Overall status is the worst finding: `NOT_READY` > `UNKNOWN` > `READY_DEGRADED` > `READY`.
- An empty expectation list fails closed (`UNKNOWN`).
- When the Phase 2 flag is off, `BeaconSession.preflight` returns `UNKNOWN` and does not log a ready call.

## Suggested checklist

Declare these as expectations, then have sibling libraries or robot code `report(...)` them:

- Required Hubs present
- Optional Hubs present or absent (absence is degraded, not blocking)
- Cameras delivering frames (ViDAR)
- Required sensors plausible (MIMIC)
- Gamepads assigned **where observable**
- Battery acceptable (AMPER)
- Loop timing acceptable
- Localization initialized (Pedro)
- Mechanisms calibrated where required (MIMIC)

## False positives to test

- Optional Expansion Hub unplugged → `READY_DEGRADED`, not `NOT_READY`
- Camera unplugged while marked optional → degraded vision, driving still allowed
- Missing Driver Station heartbeat in OpMode → `UNKNOWN` for DS freshness, not a fabricated `NOT_READY` network failure
