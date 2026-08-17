# Communications health

BEACON teaches students to ask a precise question: **which communication path failed, and what evidence do we actually have?**

A robot that “disconnects” is not one problem. Gamepad USB, Driver Station Wi-Fi, Control Hub I/O, Expansion Hub cabling, USB cameras, I²C buses, software loops, and electrical brownouts can all look similar from the driver station.

## How to use this folder

1. Read the [glossary](glossary.md).
2. Read [failure domains](failure-domains.md).
3. Read the [Driver Station freshness study](driver-link.md) before proposing any safe-stop.
4. Use [phases](phases.md) as the enablement order. Do not skip ahead.
5. Keep [rules](rules.md) in view whenever network tooling is discussed.

## Current teaching goal (Phase 0)

**What does “connected” actually mean?**

It is not the same as:

- the OpMode loop still running;
- a gamepad stick holding a value;
- telemetry still being *sent* from robot code;
- a Hub LED remaining lit.

See [research](research.md) and [driver-link.md](driver-link.md).

Repository-wide findings from the Phase 0 scaffold review live in the [initial deep audit](../audits/initial-deep-audit.md) and [priority ledger](../audits/priority-ledger.md).
