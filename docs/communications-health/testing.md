# Testing

## Unit tests (Phase 0, implemented)

Covered now: health-state construction, freshness thresholds, hysteresis windows, confidence absence, rolling event history, command-lease expiration, recovery inhibit, neutral-control detection, bounded retry math, missing data, fake clocks, registry clock-advance aging (`STALE` / `LOST` without a new report), consecutive report counts, timestamp `0` → `UNKNOWN`, preflight required vs optional (optional absence `READY_DEGRADED`, required missing evidence `UNKNOWN`, required `LOST`/`STALE` `NOT_READY`).

## Simulation tests (Phase 0, partial)

Desktop tests model ordinary jitter, complete source loss, and held-forward input that must not count as freshness. They do **not** simulate Robocol.

Later simulations should add: gamepad disconnect without Wi-Fi loss; Expansion Hub loss; camera flap; I²C freeze; loop overrun; brownout followed by Hub loss; ESD-like USB reset; misleading simultaneous failures.

## Robot tests (not started)

Safe test cards, all with adult supervision:

| Card | Restraints | Immediate stop |
|------|------------|----------------|
| Preflight validation | Robot disabled | Any unexpected motion |
| Camera disconnect | Disabled | — |
| Sensor disconnect | Disabled | — |
| Expansion Hub disconnect | Disabled | — |
| Controlled DS disconnect | **Wheels off ground** / restraints | Any uncommanded motion |
| Low-speed drivetrain safe stop | Off ground first, then low speed, exclusion area | Latency too high or stuttering |
| Recovery with non-neutral controls | Off ground | Drive resumes at old command |
| Recovery with neutral controls | Off ground | — |
| MIMIC safe-state | Mechanism supports / hard stops | Gravity drop |
| AMPER voltage correlation | Do **not** create uncontrolled brownouts | — |
| Match simulation | Standard field practices | Official e-stop / DS stop |

Do not begin communication-loss testing with an unrestrained full-speed robot. Do not intentionally create uncontrolled brownouts or ESD events.

Maximum initial drive power for any future Phase 5 test: low enough that a missed stop is a slide, not a launch. Exact percent is a team procedure, not a library default.

## Overhead

Call `BeaconSession.lastObserveDurationNanos()` in Phase 1. Acceptance requires measuring loop overhead on a Control Hub before enabling logging-heavy phases.
