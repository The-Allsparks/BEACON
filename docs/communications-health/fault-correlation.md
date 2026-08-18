# Fault correlation

Phases 3–4. Initially a **timeline**, not a root-cause classifier.

## Phase 3 automatic history

When `BeaconFeatureFlags.eventHistory()` (or `phase3EventHistory(true)`) is set, `BeaconSession` records:

- `HEALTH_TRANSITION` when a link’s `LinkState` changes, including the first observation (`NONE->…`). Unchanged states are not logged again.
- `LOOP_TIMING` once per `observe()` call. `snapshot()` is a read and does not write history.

The logger is a bounded in-memory ring (default session capacity **256**). Oldest events are dropped; `droppedCount()` is the only signal that history was truncated. Runtime does not write a second on-disk log stream.

Call `observe()` once per OpMode loop if you want aging (`STALE` / `LOST` without a new report) to appear on the timeline. Reports also record transitions so tests and infrequent reporters do not need a loop tick.

This is not a diagnosis. Events are timestamps, types, link ids, domains, and short details. Overhead on a Control Hub is still unmeasured ([issue #10](https://github.com/The-Allsparks/BEACON/issues/10)).

## Correlate, do not accuse

Join timestamps for:

- health transitions
- AMPER voltage events
- MIMIC mechanism activity
- ViDAR camera events
- Pedro localization status
- loop duration
- exceptions
- driver-input status (without treating it as a heartbeat)

## Advisory labels (Phase 4)

- probable power-related disruption
- probable isolated camera failure
- probable Expansion Hub path failure
- probable loop overrun
- insufficient evidence

Every label needs a confidence and the evidence list. Simultaneous failures are often **misleading** (brownout plus Hub loss plus camera USB reset). Prefer `INSUFFICIENT_EVIDENCE` over a dramatic story.

## Shadow safe state

Evaluate what **would** happen if intervention were enabled. Log `SHADOW_SAFE_STATE` with `Output intervention: DISABLED`.
