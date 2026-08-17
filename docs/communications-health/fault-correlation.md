# Fault correlation

Phases 3–4. Initially a **timeline**, not a root-cause classifier.

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
