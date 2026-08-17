# BEACON

**Bus and External-link Awareness, Continuity, Observation, and Notification for FTC**

BEACON is a phased communications-health and safe-state framework for FIRST
Tech Challenge robots.

It begins by passively recording whether expected Hubs, cameras, sensors,
localization sources, and software loops are healthy. Teams can then add
preflight checks, warnings, fault correlation, communication-loss safe states,
bounded device recovery, and carefully defined degraded operation.

BEACON does not replace the FTC SDK watchdog or communication protocol. It
helps teams understand communication failures, respond safely where supported,
and avoid automatically resuming stale commands after connectivity returns.

---

## Built by The Allsparks

BEACON is created and maintained by **[The Allsparks](https://github.com/The-Allsparks)** (FTC Team **#36117**).

It complements the team’s software ecosystem:

* **[ViDAR](https://github.com/The-Allsparks/ViDAR)** provides field perception and reports camera/pipeline health.
* **Pedro Pathing** provides chassis localization and reports localization health.
* **[MIMIC](https://github.com/The-Allsparks/MIMIC)** manages mechanism lifecycle and defines mechanism-specific safe states.
* **[AMPER](https://github.com/The-Allsparks/AMPER)** monitors electrical health and identifies power events that may resemble communication failures.
* **BEACON** determines which communication paths and data sources remain trustworthy and coordinates communication-loss response.

Repository: **[The-Allsparks/BEACON](https://github.com/The-Allsparks/BEACON)**

> **Disclaimer:** BEACON is community-developed and unofficial. It is **not** affiliated with or endorsed by FIRST, REV Robotics, CTRE, NI, or other referenced vendors. Teams must verify legality and performance against the current-season FTC Game Manual.

---

## Current status

| Item | Status |
|------|--------|
| **Version** | `0.1.0-SNAPSHOT` |
| **Implemented phase** | **Phase 0** (vocabulary, immutable reports, fake clocks, manual registry, passive logging types) |
| **Phase 1** | Designed; flag exists (`BeaconFeatureFlags.manualReports()`), not hardware-validated |
| **Phases 2–10** | Designed / experimental / **disabled by default** |
| **Active motor, servo, or network intervention** | **Disabled.** Do not enable without review and acceptance tests. |
| **Driver Station early safe-stop** | **Not implemented.** No reliable supported public freshness API was verified. See [driver-link.md](docs/communications-health/driver-link.md). |
| **Production safety claims** | **None.** This scaffold has not been validated on a real FTC robot. |

**No active behavior should be enabled without controlled testing.**

Supported targets for this scaffold:

* **FTC SDK:** current public [FtcRobotController](https://github.com/FIRST-Tech-Challenge/FtcRobotController) season releases (Java TeamCode integration). Desktop tests compile against Java 11 without the SDK on the classpath. Research cites RobotCore **11.2.0** Javadocs.
* **Hardware:** REV Control Hub, Expansion Hub, and Driver Hub as documented by REV and FIRST. BEACON does not command those devices in Phase 0.
* **Library build:** Java 11 source/target; CI uses Temurin 17 to compile and test.

### Current limitations

* Phase 0 provides identifiers, health states, reason codes, timestamps, confidence semantics, fake clocks, fake health sources, a manual registry, and bounded logging. It does **not** change motor or servo output, restart hardware, or modify the official network.
* Ordinary OpMode code **cannot** read Driver Station heartbeat objects, peer-connected events, or Control Hub keepalive state. Those exist in SDK internals, system logs, or Hub LEDs. See [driver-link.md](docs/communications-health/driver-link.md).
* BEACON **cannot** repair poor wiring, damaged connectors, radio interference, ESD, or brownouts.
* BEACON does **not** claim to identify intentional jamming.
* Safe-stop support depends on a trustworthy and timely indication of communication freshness. That indication is **not** available through supported public OpMode APIs as of this research.
* BEACON is **not** a self-healing network system.

### Relationship to official FTC watchdog behavior

The FTC Robot Controller already stops an OpMode when Driver Station communication is lost for a documented interval on the order of **two seconds** (official troubleshooting: the Driver Station treats more than two seconds without communication as a disconnect; heartbeats are described as occurring about every tenth of a second). Hub firmware independently enters a keepalive-timeout fail-safe (blinking blue LED). BEACON must not weaken, replace, or circumvent those behaviors.

### Software cannot replace physical reliability

BEACON cannot replace:

* secure Control Hub placement and antenna-aware structure;
* USB strain relief and short camera cables;
* a secure Expansion Hub communication cable;
* protected Driver Hub USB ports;
* current firmware and matched RC/DS versions;
* appropriate 5 GHz use and event-directed channel configuration;
* ESD mitigation, including REV’s guidance to prefer the Control Hub USB 3.0 port for cameras;
* healthy batteries, tight XT30 connections, and preventing Driver Hub sleep.

---

## Documentation

| Doc | Purpose |
|-----|---------|
| [Communications-health overview](docs/communications-health/README.md) | Student entry point |
| [Research](docs/communications-health/research.md) | Source-backed findings |
| [Build vs adopt](docs/communications-health/build-vs-adopt.md) | Why BEACON is a standalone layer |
| [Architecture](docs/communications-health/architecture.md) | Module boundaries |
| [Failure domains](docs/communications-health/failure-domains.md) | Independent communication paths |
| [Phases](docs/communications-health/phases.md) | Phase goals and acceptance |
| [Preflight](docs/communications-health/preflight.md) | Match-readiness inspection |
| [Driver-link feasibility](docs/communications-health/driver-link.md) | Public API study |
| [Command freshness](docs/communications-health/command-freshness.md) | Command leases |
| [Safe state](docs/communications-health/safe-state.md) | Subsystem-specific rest |
| [Recovery](docs/communications-health/recovery.md) | Bounded retry |
| [Degraded operation](docs/communications-health/degraded-operation.md) | Honest fallbacks |
| [Fault correlation](docs/communications-health/fault-correlation.md) | Timelines, not root-cause claims |
| [AMPER integration](docs/communications-health/amper-integration.md) | Electrical vs communications |
| [MIMIC integration](docs/communications-health/mimic-integration.md) | Mechanism safe states |
| [ViDAR integration](docs/communications-health/vidar-integration.md) | Camera health |
| [Testing](docs/communications-health/testing.md) | Unit / sim / robot procedures |
| [Troubleshooting](docs/communications-health/troubleshooting.md) | Physical and software checks |
| [Glossary](docs/communications-health/glossary.md) | Vocabulary |
| [References](docs/communications-health/references.md) | Citation table |
| [Examples](examples/README.md) | Integration sketches |
| [Phase 0 file plan](docs/communications-health/phase-0-plan.md) | Exact implementation plan |
| [Assessment](docs/communications-health/assessment.md) | Benefit vs complexity judgment |
| [Risks](docs/communications-health/risks.md) | Open questions |
| [Conventions](docs/communications-health/conventions.md) | Organization convention assessment |
| [Rules](docs/communications-health/rules.md) | Competition-manual constraints |
| [Exercises](docs/communications-health/exercises.md) | Student practice |
| [Initial deep audit](docs/audits/initial-deep-audit.md) | Orchestrator audit of purpose, architecture, safety, and gaps |
| [Priority ledger](docs/audits/priority-ledger.md) | Ready-issue selection and PR status |

---

## Quick start (desktop)

```powershell
git clone https://github.com/The-Allsparks/BEACON.git
cd BEACON
.\gradlew.bat test
```

On Linux/macOS:

```bash
./gradlew test
```

---

## Design principles

1. **Observe.** Record whether expected links and data sources are healthy.
2. **Explain.** Every result says why, including when evidence is missing.
3. **Warn.** Feature-flagged, reversible, and disabled by default.
4. **Protect narrowly.** Only after a supported freshness signal is proven.
5. **Assist with bounded recovery.** Never loop indefinitely or block the robot loop.
6. **Coordinate.** Request safe state; do not command every motor.
7. **Predict only if evidence supports prediction.**
8. **Adapt only if behavior remains understandable.**

No advanced feature is required to use an earlier phase. Removing BEACON must not require rewriting the robot.

---

## License

MIT — same open-source license family as [ViDAR](https://github.com/The-Allsparks/ViDAR), [AMPER](https://github.com/The-Allsparks/AMPER), and [MIMIC](https://github.com/The-Allsparks/MIMIC). See [LICENSE](LICENSE).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md), [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md), and [SECURITY.md](SECURITY.md).
