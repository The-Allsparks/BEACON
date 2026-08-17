# Preflight

Phase 2. Types exist; the inspector is not enabled.

## Purpose

Find problems **before** the match. The checklist must not command actuators.

## Possible results

| Status | Meaning |
|--------|---------|
| `READY` | All required items have explanations that support go |
| `READY_DEGRADED` | Optional item failed or is absent; robot may play with documented limits |
| `NOT_READY` | A required item failed |
| `UNKNOWN` | Evidence missing; fail closed for required items |

Every finding must include a non-empty explanation.

## Suggested checklist

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
