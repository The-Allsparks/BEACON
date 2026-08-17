# Assessment

Evidence-based judgment as of 2026-08-17. See [research.md](research.md), [driver-link.md](driver-link.md), [build-vs-adopt.md](build-vs-adopt.md), and [rules.md](rules.md).

## Is BEACON justified as a standalone project?

**Yes**, as a vocabulary, registry, correlation, and (later) coordination layer for The Allsparks ecosystem. It is **not** justified as an autonomous network manager or as a replacement for the FTC watchdog.

## What FTC already handles

- Robocol DS↔RC protocol
- Heartbeats and peer disconnect inside the RC app
- OpMode stop after documented communication loss
- Hub keepalive fail-safe (LED + firmware)
- Module unresponsive tracking / USB recovery
- DS gamepad rebinding helpers (where serials exist)
- Match enable/disable and official stop

## Supported public APIs vs logs/internal

**OpMode-visible:** gamepad state and change timestamp, official stop flags, Lynx `isNotResponding`, device health enums, user-measured loop time, sensor/camera APIs, voltage sensors.

**Logs/UI/internal:** heartbeat objects, peer events, DS ping, Hub LEDs, REV Wi-Fi logs, RC disconnect strings, interference attribution.

## Reliable early Driver Station loss detection?

**Not with supported public APIs.** `Gamepad.timestamp` is not a heartbeat. Loop iteration is not a packet. Telemetry outbound is not inbound freshness. Internal `EventLoopManager` APIs are the wrong surface for a competition library.

## Expected safe-stop latency

If using **only** official behavior: on the order of **2 seconds** after DS silence (wiki; not re-measured here), plus however long Hub keepalive takes (unpublished to OpModes). Motors may continue until that stack notices.

A faster library stop would require an unsupported signal and is **out of scope**.

## Rookie-team phases

Phases **0–2** (vocabulary, manual reports, preflight) plus the physical checklist. Highest teaching value per complexity.

## Best benefit for complexity (this team)

1. Phase 0–1 reports from ViDAR/MIMIC/AMPER/Pedro (cheap, reversible)
2. Phase 2 preflight (prevents “we noticed in match 1”)
3. Phase 3 timelines (debug ESD vs brownout vs cable)
4. Phase 4 shadow (learn without moving motors)

## Wait until later in the season

Phases 5–9. Phase 5 is blocked on a supported DS freshness API. Phase 7 camera recovery only after ViDAR is stable. Phase 9 collision guard is optional and easy to get wrong.

## Integration

BEACON consumes health; it does not own perception, mechanisms, power, or pathing. Manual `report(...)` first, adapters later.

## Recovery

**Safe to consider later:** bounded, non-blocking camera reopen.

**Remain manual:** encoder reset, Hub config, calibration, power cycles, Wi-Fi channel, anything that fights official stop.

## SystemCore

**Verified fact:** none for communications APIs in this research.

**Possibility:** richer health or connection APIs might appear. Revisit issue 27 when FIRST/REV publish authoritative docs. Do not code to rumors.
