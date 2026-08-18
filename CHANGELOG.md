# Changelog

All notable changes to BEACON will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project aims to adhere to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Initial public repository scaffold for The Allsparks FTC Team 36117.
- Phase 0 implemented: identifiers, health states, immutable reports, fake clocks, fake health sources, manual registry, command-lease types, recovery-inhibit types, and bounded event logging.
- Source-backed communications-health research, Driver Station freshness feasibility study, architecture, phased roadmap, and student documentation.
- Initial deep audit and priority ledger under `docs/audits/`.
- CI for compile, unit tests, and relative documentation link checks.
- Phase 2 `PreflightInspector`: required vs optional expectations against registry snapshots.
- Phase 3 event history: when the flag is on, `BeaconSession` records `HEALTH_TRANSITION` on state changes and `LOOP_TIMING` on `observe()`. Default in-memory capacity remains 256.
- Phase 4 advisory correlator: `EventCorrelator` emits one label with confidence and evidence. Simultaneous multi-family failures without electrical/AMPER evidence are `INSUFFICIENT_EVIDENCE`. No jamming label.

### Changed

- Stored health snapshots now apply `FreshnessPolicy` at sample time. A timestamp of `0` is `UNKNOWN`, not `HEALTHY`. Consecutive success/failure counts accumulate across reports.

### Security

- CI GitHub Actions (`actions/checkout`, `actions/setup-java`) are pinned to full commit SHAs with version comments.

### Safety

- All motor, servo, hardware-recovery, and network-intervention features remain disabled by default.
- No production Driver Station safe-stop is implemented; public OpMode APIs were not verified to provide a reliable freshness heartbeat.
