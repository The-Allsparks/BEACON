# Phases

No advanced feature is required to use an earlier phase. Each active feature must be separately flagged, independently testable, observable, reversible, fail-safe when data is missing, disabled by default, and accompanied by a student explanation plus a physical test.

## Phase 0 — Research and common vocabulary

**Teach:** What does “connected” actually mean?

Implement: identifiers, health states, reason codes, timestamps, confidence, fake clocks, fake health sources, immutable reports, documentation.

**Acceptance:** no hardware behavior changes; states have precise definitions; unsupported data remains explicitly unavailable; Driver Station freshness study is complete.

**Status:** implemented in this scaffold.

## Phase 1 — Manually reported passive health

**Teach:** Which parts of the robot communicate with which other parts?

Allow ViDAR, MIMIC, AMPER, Pedro, and robot code to submit reports. BEACON only displays and logs.

**Acceptance:** reports are time-correlated; BEACON does not probe or restart devices; integration can be removed without subsystem rewrites; loop overhead is measured.

**Status:** time-correlated sampling implemented in the registry; flag default off; Control Hub overhead not measured.

## Phase 2 — Preflight inspection

**Teach:** How can we find a problem before the match begins?

Report required/optional Hubs, cameras, sensors, gamepads where observable, AMPER battery, loop timing, localization, calibration.

**Acceptance:** every failure explains why; optional failures yield `READY_DEGRADED`; checklist does not command actuators; false positives tested.

**Status:** inspector implemented against the health registry; flag default off; no hardware probing.

## Phase 3 — Passive event history

**Teach:** What happened immediately before the failure?

Rolling timeline of health, loop duration, AMPER voltage, mechanism activity, camera timing, localization, exceptions. No root-cause claims.

**Acceptance:** post-match timelines distinguish major domains; logging is bounded.

**Status:** bounded logger records `HEALTH_TRANSITION` and `LOOP_TIMING` when the Phase 3 flag is on; default session capacity is 256; flag default off; Control Hub overhead not measured.

## Phase 4 — Advisory correlation and shadow safe state

**Teach:** What evidence supports a diagnosis?

Advisory labels only: probable power disruption, isolated camera failure, Expansion Hub path failure, loop overrun, insufficient evidence. Shadow:

```text
Would enter SAFE_STOP
Reason: verified driver-link freshness exceeded threshold
Output intervention: DISABLED
```

**Acceptance:** classifications include confidence; shadow through full matches; no outputs change.

## Phase 5 — Drivetrain communication safe stop

**Teach:** Why should a command expire?

Implement **only if** Phase 0 proves a supported Driver Station freshness source. Current feasibility study: **not proven**. See [driver-link.md](driver-link.md).

If later enabled: teleop drivetrain only; zero motion; invalidate prior command; recovery inhibit; wheels off the ground for tests.

## Phase 6 — MIMIC safe-state integration

**Teach:** Why does “safe” mean different things for different mechanisms?

BEACON requests; MIMIC decides mechanically safe action.

## Phase 7 — Bounded local recovery

**Teach:** When is automatic retry helpful, and when is it dangerous?

Start with a single ViDAR camera. Attempt limits, backoff, timeout, CPU budget, degraded fallback. Never reset encoders or Hub configuration automatically.

## Phase 8 — Degraded operation

**Teach:** How can the robot remain useful without pretending everything works?

Consumers respond to health. Loss of confidence cannot silently produce confident autonomous action.

## Phase 9 — Optional local collision guard

**Teach:** How can a local sensor provide a final layer when communication fails?

Narrow emergency stop from trusted local sensors. Cannot command motion. Separate from DS loss detection.

## Phase 10 — Post-match diagnostic tooling

**Teach:** How can several incomplete logs tell one complete story?

Combine BEACON, AMPER, MIMIC, ViDAR, Pedro, RC logs, REV Wi-Fi logs, and DS evidence where legal. Not required for runtime safety.
