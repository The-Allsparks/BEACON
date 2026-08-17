# Recovery

Phase 7. Disabled by default. Phase 0 only stores `RecoveryPolicy`.

## Policy fields

- whether recovery is allowed
- maximum attempts
- backoff (exponential, capped)
- timeout
- CPU/time budget
- success criteria
- permanent degraded state

## Safe to consider (later, after tests)

- restarting a **single** UVC camera session while other cameras continue
- re-opening an I²C device that MIMIC marks as recoverable **without** encoder reset

## Remain manual

- encoder resets
- Hub configuration / address changes
- calibration-sensitive devices
- Expansion Hub power cycles
- Wi-Fi channel changes during a match
- any recovery that blocks the main robot loop
- any recovery that fights the official watchdog

## Acceptance (when implemented)

- recovery cannot loop indefinitely;
- remaining ViDAR cameras continue;
- recovery does not block the main loop;
- failed recovery produces a stable degraded state.
