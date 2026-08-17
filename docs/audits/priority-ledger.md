# BEACON priority ledger

Living tracker for orchestrator selection. Update after each merged PR or when issue readiness changes.

**Last updated:** 2026-08-17  
**Audited commit:** `56a85b5` (`main` after PR #28)  
**Automatic merge:** false (human approval required)  
**Max active implementation subagents:** 1  
**Open implementation PR:** issue #30 on `phase-1/30-freshness-sampling`

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

PR #28 is merged. Current implementation: [#30](https://github.com/The-Allsparks/BEACON/issues/30) freshness-aware sampling.

| Issue | Priority | Readiness | Dependencies | Current status | Assigned subagent | Branch | Pull request | CI status | Merge status | Blocker | Next action |
|-------|----------|-----------|--------------|----------------|-------------------|--------|--------------|-----------|--------------|---------|-------------|
| [#28](https://github.com/The-Allsparks/BEACON/pull/28) Phase 0 scaffold | done | merged | none | Merged to `main` | — | `scaffold/phase-0` | [#28](https://github.com/The-Allsparks/BEACON/pull/28) | success | merged | none | Close #6–#9 |
| [#30](https://github.com/The-Allsparks/BEACON/issues/30) Freshness-aware sampling | 1 | Ready | #9, #10 software | Implementing | orchestrator | `phase-1/30-freshness-sampling` | pending | pending | — | none | Test, open PR, wait for CI |
| [#32](https://github.com/The-Allsparks/BEACON/issues/32) Branch protection | 2 | Blocked on human policy | none | Open | — | n/a | — | — | — | Who must approve? | Maintainer decision |
| [#31](https://github.com/The-Allsparks/BEACON/issues/31) Pin Actions SHAs | 3 | Ready after #30 PR resolves | none | Open | — | — | — | — | — | Open #30 PR | Follow-up PR |
| [#10](https://github.com/The-Allsparks/BEACON/issues/10) Registry overhead AC | 4 | Blocked on hardware | none | Open; software done | — | — | — | — | — | Control Hub | Measure when available |
| [#11](https://github.com/The-Allsparks/BEACON/issues/11)–[#14](https://github.com/The-Allsparks/BEACON/issues/14) Sibling reports | 5 | Blocked on sibling DTOs | #10 | Open; docs exist | — | — | — | — | — | Sibling contracts | Manual `HealthReport` only |
| [#15](https://github.com/The-Allsparks/BEACON/issues/15) Preflight inspector | 6 | Not ready | #30 | Open | — | — | — | — | — | Time-honest registry | After #30 merges |
| [#16](https://github.com/The-Allsparks/BEACON/issues/16) Auto event history | 7 | Not ready | #10 overhead | Open; logger exists | — | — | — | — | — | Overhead unknown | After measurement or bound |
| [#3](https://github.com/The-Allsparks/BEACON/issues/3) Stop latency | 8 | Blocked on hardware | adult supervision | Open | — | — | — | — | — | Restrained robot | Do not fake as hardware |
| [#2](https://github.com/The-Allsparks/BEACON/issues/2)/[#20](https://github.com/The-Allsparks/BEACON/issues/20)/[#21](https://github.com/The-Allsparks/BEACON/issues/21) DS safe-stop | 9 | Blocked | public freshness API | Open | — | — | — | — | — | Readiness gate unmet | Do not implement |
| [#27](https://github.com/The-Allsparks/BEACON/issues/27) SystemCore | 10 | Blocked | authoritative docs | Open | — | — | — | — | — | No docs | Keep unavailable |
| [#29](https://github.com/The-Allsparks/BEACON/issues/29) Roadmap epic | tracking | n/a | none | Open | orchestrator | n/a | n/a | n/a | n/a | none | Update after each merge |

## Phase 0 issue hygiene (software vs remaining AC)

Implemented in PR #28 tree; close **after merge** if remaining ACs are explicitly deferred:

| Issue | Software in #28 | Remaining AC | Close on merge? |
|-------|-----------------|--------------|-----------------|
| #1 DS/RC heartbeat research | yes | Re-verify next SDK | No — leave open for SDK bump |
| #2 Public DS freshness API | study yes | Hardware cadence | No — blocked/hardware |
| #3 Stop latency | procedure only | Measurement | No — hardware |
| #4 Gamepad timestamp | Javadoc yes | Hardware hold test | No — hardware |
| #5 Module disconnect | research yes | Season SDK confirm | No — optional hardware |
| #6 AdvantageKit/FRC patterns | yes | none | **Yes** |
| #7 Build vs adopt | yes | none | **Yes** |
| #8 Health-state vocabulary | yes | none | **Yes** |
| #9 Immutable LinkHealth | yes | none | **Yes** |
| #19 Command lease type | yes | Classroom exercise | No — learning AC |

## Stop conditions currently in effect

- Do not merge without human approval.
- Do not implement Phase 5–9 active behavior.
- Do not claim hardware validation.
- One implementation PR at a time.
