# Changelog

All notable changes to BEACON will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project aims to adhere to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Initial public repository scaffold for The Allsparks FTC Team 36117.
- Phase 0 implemented: identifiers, health states, immutable reports, fake clocks, fake health sources, manual registry, command-lease types, recovery-inhibit types, and bounded event logging.
- Source-backed communications-health research, Driver Station freshness feasibility study, architecture, phased roadmap, and student documentation.
- CI for compile, unit tests, and relative documentation link checks.

### Safety

- All motor, servo, hardware-recovery, and network-intervention features remain disabled by default.
- No production Driver Station safe-stop is implemented; public OpMode APIs were not verified to provide a reliable freshness heartbeat.
