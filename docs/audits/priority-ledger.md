# BEACON priority ledger

Living tracker for orchestrator selection. Update after each merged PR or when issue readiness changes.

**Last updated:** 2026-08-17  
**Audited commit:** `984f47a69e7c50f4bb9724fb5d626ddaab6201e4`  
**Automatic merge:** false (human approval required)  
**Max active implementation subagents:** 1  
**Open implementation PR:** [#28](https://github.com/The-Allsparks/BEACON/pull/28) (draft → ready after audit docs land)

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

Until PR #28 is merged, **do not start a second implementation PR**. Highest-priority next software issue: [#30](https://github.com/The-Allsparks/BEACON/issues/30) (audit A1/C1/T1).

| Issue | Priority | Readiness | Dependencies | Current status | Assigned subagent | Branch | Pull request | CI status | Merge status | Blocker | Next action |
|-------|----------|-----------|--------------|----------------|-------------------|--------|--------------|-----------|--------------|---------|-------------|
| [#28](https://github.com/The-Allsparks/BEACON/pull/28) Phase 0 scaffold | 1 | Ready for human review after audit docs | none | Draft PR | orchestrator | `scaffold/phase-0` | [#28](https://github.com/The-Allsparks/BEACON/pull/28) | prior success; re-run after audit push | not authorized | `AUTOMATIC_MERGE=false` | Push audit docs; mark ready; request human merge |
| [#30](https://github.com/The-Allsparks/BEACON/issues/30) Freshness-aware sampling | 2 | Technically ready; process-wait on #28 | #9, #10, PR #28 | Open | review until #28 resolves | — | — | — | — | Open implementation PR #28 | Subagent review first |
| [#32](https://github.com/The-Allsparks/BEACON/issues/32) Branch protection | 3 | Blocked on human policy | none | Open | — | n/a | — | — | — | Who must approve? | Maintainer decision |
| [#31](https://github.com/The-Allsparks/BEACON/issues/31) Pin Actions SHAs | 4 | Ready after #28 | none | Open | — | — | — | — | — | Open PR #28 | Follow-up PR |
| [#10](https://github.com/The-Allsparks/BEACON/issues/10) Registry overhead AC | 5 | Blocked on hardware | PR #28 | Open; software done | — | — | — | — | — | Control Hub | Measure when available |
| [#11](https://github.com/The-Allsparks/BEACON/issues/11)–[#14](https://github.com/The-Allsparks/BEACON/issues/14) Sibling reports | 6 | Blocked on sibling DTOs | #10 | Open; docs exist | — | — | — | — | — | Sibling contracts | Manual `HealthReport` only |
| [#15](https://github.com/The-Allsparks/BEACON/issues/15) Preflight inspector | 7 | Not ready | #30 | Open | — | — | — | — | — | Time-honest registry | After #30 |
| [#16](https://github.com/The-Allsparks/BEACON/issues/16) Auto event history | 8 | Not ready | #10 overhead | Open; logger exists | — | — | — | — | — | Overhead unknown | After measurement or bound |
| [#3](https://github.com/The-Allsparks/BEACON/issues/3) Stop latency | 9 | Blocked on hardware | adult supervision | Open | — | — | — | — | — | Restrained robot | Do not fake as hardware |
| [#2](https://github.com/The-Allsparks/BEACON/issues/2)/[#20](https://github.com/The-Allsparks/BEACON/issues/20)/[#21](https://github.com/The-Allsparks/BEACON/issues/21) DS safe-stop | 10 | Blocked | public freshness API | Open | — | — | — | — | — | Readiness gate unmet | Do not implement |
| [#27](https://github.com/The-Allsparks/BEACON/issues/27) SystemCore | 11 | Blocked | authoritative docs | Open | — | — | — | — | — | No docs | Keep unavailable |
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
- Do not open a second implementation PR while #28 is unresolved.
