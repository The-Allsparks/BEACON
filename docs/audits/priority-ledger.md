# BEACON priority ledger

Living tracker for orchestrator selection. Update after each merged PR or when issue readiness changes.

**Last updated:** 2026-08-18  
**Audited commit:** `7b27023` (`main` after PR #40)  
**Automatic merge:** false except when the user explicitly says to proceed or continue on a ready PR  
**Max active implementation subagents:** 1  
**Open implementation PR:** issue #17 software slice on `phase-4/17-advisory-correlation`

## Priority model

Evaluate ready issues by: safety, correctness, dependency-unblocking value, architectural leverage, user value, learning value, risk reduction, implementation confidence, effort, hardware dependency, external dependency.

Default order:

1. Safety blockers
2. Correctness blockers
3. CI or build failures
4. Issues blocking multiple other issues
5. Architectural seams needed by later work
6. Missing tests for upcoming work
7. Small complete user-facing improvements
8. Performance work supported by measurements
9. Documentation and usability
10. Optional advanced capabilities
11. Cosmetic cleanup

An issue is **ready** only when requirements are clear, dependencies are resolved, acceptance criteria are testable, credentials exist, hardware is available or unnecessary, and no overlapping implementation PR is open.

## Current selection

PR #40 is merged (`Closes #16` software; overhead stays on #10). Current implementation: [#17](https://github.com/The-Allsparks/BEACON/issues/17) advisory correlator (software fixtures). Match false-fault review and sibling adapters remain open on #17. Shadow safe-stop is [#18](https://github.com/The-Allsparks/BEACON/issues/18).

| Issue | Priority | Readiness | Dependencies | Current status | Assigned subagent | Branch | Pull request | CI status | Merge status | Blocker | Next action |
|-------|----------|-----------|--------------|----------------|-------------------|--------|--------------|-----------|--------------|---------|-------------|
| [#28](https://github.com/The-Allsparks/BEACON/pull/28) Phase 0 scaffold | done | merged | none | Merged | — | `scaffold/phase-0` | [#28](https://github.com/The-Allsparks/BEACON/pull/28) | success | merged | none | done |
| [#30](https://github.com/The-Allsparks/BEACON/issues/30) Freshness-aware sampling | done | merged | #9, #10 software | Merged | — | `phase-1/30-freshness-sampling` | [#37](https://github.com/The-Allsparks/BEACON/pull/37) | success | merged | none | done |
| [#31](https://github.com/The-Allsparks/BEACON/issues/31) Pin Actions SHAs | done | merged | none | Merged | — | `repo/31-pin-actions-shas` | [#38](https://github.com/The-Allsparks/BEACON/pull/38) | success | merged | none | done |
| [#15](https://github.com/The-Allsparks/BEACON/issues/15) Preflight inspector | done | merged | #30 | Merged | — | `phase-2/15-preflight-inspector` | [#39](https://github.com/The-Allsparks/BEACON/pull/39) | success | merged | none | done |
| [#16](https://github.com/The-Allsparks/BEACON/issues/16) Auto event history | done | merged | logger + #30 | Merged | — | `phase-3/16-event-history` | [#40](https://github.com/The-Allsparks/BEACON/pull/40) | success | merged | none | Overhead remains #10 |
| [#17](https://github.com/The-Allsparks/BEACON/issues/17) Advisory correlator | 1 | Ready (software split) | #16 | Implementing | orchestrator | `phase-4/17-advisory-correlation` | pending | pending | — | Match review; sibling DTOs | Test, open PR; do not close #17 |
| [#18](https://github.com/The-Allsparks/BEACON/issues/18) Shadow safe-stop | 2 | Blocked | public freshness / match logs | Open | — | — | — | — | — | No public DS heartbeat | Do not invent a detector |
| [#32](https://github.com/The-Allsparks/BEACON/issues/32) Branch protection | 3 | Blocked on human policy | none | Open | — | n/a | — | — | — | Who must approve? | Maintainer decision |
| [#10](https://github.com/The-Allsparks/BEACON/issues/10) Registry / loop overhead | 4 | Blocked on hardware | none | Open; software done | — | — | — | — | — | Control Hub | Measure when available |
| [#11](https://github.com/The-Allsparks/BEACON/issues/11)–[#14](https://github.com/The-Allsparks/BEACON/issues/14) Sibling reports | 5 | Blocked on sibling DTOs | #10 | Open; docs exist | — | — | — | — | — | Sibling contracts | Manual `HealthReport` only |
| [#3](https://github.com/The-Allsparks/BEACON/issues/3) Stop latency | 6 | Blocked on hardware | adult supervision | Open | — | — | — | — | — | Restrained robot | Do not fake as hardware |
| [#2](https://github.com/The-Allsparks/BEACON/issues/2)/[#20](https://github.com/The-Allsparks/BEACON/issues/20)/[#21](https://github.com/The-Allsparks/BEACON/issues/21) DS safe-stop | 7 | Blocked | public freshness API | Open | — | — | — | — | — | Readiness gate unmet | Do not implement |
| [#27](https://github.com/The-Allsparks/BEACON/issues/27) SystemCore | 8 | Blocked | authoritative docs | Open | — | — | — | — | — | No docs | Keep unavailable |
| [#29](https://github.com/The-Allsparks/BEACON/issues/29) Roadmap epic | tracking | n/a | none | Open | orchestrator | n/a | n/a | n/a | n/a | none | Update after each merge |
| Dependabot #33–#35 | deferred | not selected | compatibility analysis | Open PRs | — | dependabot/* | [#33](https://github.com/The-Allsparks/BEACON/pull/33)–[#35](https://github.com/The-Allsparks/BEACON/pull/35) | unknown | — | Major Gradle/JUnit/Actions upgrades | Do not merge without analysis |

Note: Dependabot [#36](https://github.com/The-Allsparks/BEACON/pull/36) already merged `setup-java` **v5.7.0** onto `main` after the original v4 pin from #31.

## Phase 0 issue hygiene (software vs remaining AC)

Implemented in PR #28 tree; close **after merge** if remaining ACs are explicitly deferred:

| Issue | Software in #28 | Remaining AC | Close on merge? |
|-------|-----------------|--------------|-----------------|
| #1 DS/RC heartbeat research | yes | Re-verify next SDK | No — leave open for SDK bump |
| #2 Public DS freshness API | study yes | Hardware cadence | No — blocked/hardware |
| #3 Stop latency | procedure only | Measurement | No — hardware |
| #4 Gamepad timestamp | Javadoc yes | Hardware hold test | No — hardware |
| #5 Module disconnect | research yes | Season SDK confirm | No — optional hardware |
| #6 AdvantageKit/FRC patterns | yes | none | **Yes** (closed with #28) |
| #7 Build vs adopt | yes | none | **Yes** (closed with #28) |
| #8 Health-state vocabulary | yes | none | **Yes** (closed with #28) |
| #9 Immutable LinkHealth | yes | none | **Yes** (closed with #28) |
| #19 Command lease type | yes | Classroom exercise | No — learning AC |

## Stop conditions currently in effect

- Do not merge without human approval, unless the user explicitly says to proceed on a ready PR.
- Do not implement Phase 5–9 active behavior.
- Do not claim hardware validation.
- One implementation PR at a time.
- Do not merge Dependabot major bumps (#33 JUnit 6, #34 checkout v7, #35 Gradle 9) without compatibility analysis.
