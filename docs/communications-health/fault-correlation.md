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

`EventCorrelator.evaluate(events, snapshot)` returns **one** label with a confidence and an evidence list. `BeaconSession.advise()` is off unless `BeaconFeatureFlags.advisory()` (or `phase4AdvisoryShadow(true)`) is set. Disabled calls return `INSUFFICIENT_EVIDENCE` with unknown confidence.

Labels:

- probable power-related disruption
- probable isolated camera failure
- probable Expansion Hub path failure
- probable loop overrun
- insufficient evidence

Rules used in software (desktop fixtures, not match-validated):

- `STALE` / `LOST` links count as failed. `UNKNOWN` and `HEALTHY` do not.
- A **single** domain family (camera / hub-path / electrical / loop-overrun) may receive that family’s probable label at confidence `0.5`.
- Software-loop loss **without** reason `LOOP_OVERRUN` is `INSUFFICIENT_EVIDENCE`.
- Driver Station / gamepad domains are `INSUFFICIENT_EVIDENCE` (this is not a heartbeat detector).
- Two or more families without electrical or AMPER voltage evidence → `INSUFFICIENT_EVIDENCE`.
- Electrical or `AMPER_VOLTAGE` evidence **plus** other failed families → `PROBABLE_POWER_DISRUPTION` at confidence `0.4`, with all evidence listed. That is not a unique cause and is **not** jamming.
- There is no jamming or malicious-interference label.

Sibling ViDAR/MIMIC/AMPER/Pedro adapters are not required: tests and OpModes may submit `HealthReport` / `BeaconEvent` fixtures. Post-match false-fault review remains issue #17. Shadow safe-stop logging remains issue #18.

Every label needs a confidence and the evidence list. Simultaneous failures are often **misleading** (brownout plus Hub loss plus camera USB reset). Prefer `INSUFFICIENT_EVIDENCE` over a dramatic story.

## Shadow safe state

Evaluate what **would** happen if intervention were enabled. Log `SHADOW_SAFE_STATE` with `Output intervention: DISABLED`.
