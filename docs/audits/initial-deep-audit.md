# BEACON initial deep audit

**Date of audit:** 2026-08-17  
**Audited commit SHA:** `984f47a69e7c50f4bb9724fb5d626ddaab6201e4`  
**Audited branch:** `scaffold/phase-0` (open draft PR [#28](https://github.com/The-Allsparks/BEACON/pull/28))  
**Default branch:** `main` (`b280954` — repository initialization only)  
**Auditor identity:** GitHub `TA-C-GHill` (admin on `The-Allsparks/BEACON`)  
**Automatic merge:** not authorized (`AUTOMATIC_MERGE=false`)

Classification uses the orchestrator severity and type vocabularies defined at the end of this document. Findings require evidence. Hardware claims in this audit are **not** measured on a robot.

---

## Executive summary

BEACON is a Phase 0 communications-health scaffold. It matches its stated purpose: a passive vocabulary, immutable reports, a manual registry, fake clocks, and research that refuses to invent a Driver Station freshness signal. It does **not** command motors, servos, or the official FTC network. Feature flags keep later phases off. `isAnyInterventionEnabled()` is false by default.

The original design promised observe → explain → warn → protect narrowly → bounded recovery → coordinate. Only the observe/explain vocabulary and supporting types exist in code. Preflight evaluation, automatic event transitions, shadow safe-state, and all active behavior are documented and type-stubbed, not implemented.

No safety blocker was found in the current code path: there are no hardware writes, no private SDK usage, and no production DS safe-stop. The highest software gap is that stored health does not age: `FakeHealthSource.sample(nowNanos)` ignores time, and `HealthRegistry` never applies `FreshnessPolicy`. A single `HEALTHY` report remains `HEALTHY` forever. That undermines Phase 1 time-correlation and later shadow evaluation.

Repository health is early-stage: 27 open issues already map the phase roadmap, draft PR #28 has a successful CI run, `main` has **no branch protection**, GitHub Actions are tag-pinned not SHA-pinned, and there is no GitHub Project accessible with the current token scopes. Existing issues use the phase-work template, not the fuller orchestrator issue schema.

**Readiness:** Phase 0 software and research are complete enough to merge after human review of PR #28. Phase 1 software can start after that merge, except sibling-library DTO mapping and Control Hub overhead measurement. Phase 5+ remains blocked until a supported public freshness API exists. No production safety claims are justified.

---

## Project purpose

**Problem claimed:** FTC “disconnects” are several independent failure domains (gamepad USB, DS Wi-Fi, Hub I/O, Expansion Hub cable, USB cameras, I²C, software loops, electrical events). Teams need a way to record which path is trustworthy, explain missing evidence, and later coordinate safe-state — without replacing the FTC watchdog.

**Intended users:** Allsparks students and mentors; other FTC teams integrating one report at a time; later, sibling libraries (ViDAR, AMPER, MIMIC, Pedro) and TRACE/HELM consumers.

**Explicit responsibilities:**

- Vocabulary for link identity, state, domain, reason, freshness, confidence.
- Manual health registry that does not own devices.
- Bounded event logging (TRACE-compatible CSV header).
- Feature-flagged phase enablement with intervention default off.
- Research and student documentation of what OpMode code cannot observe.

**Not BEACON’s job:** FTC Robocol/watchdog, Hub keepalive, Lynx reconnect, perception (ViDAR), mechanism safe motion (MIMIC), electrical limiting (AMPER), pathing (Pedro), deterministic replay storage (TRACE), task selection (HELM), robot application OpModes.

---

## Current maturity

| Item | Status |
|------|--------|
| Version | `0.1.0-SNAPSHOT` |
| Implemented phase | Phase 0 vocabulary + types + manual registry + bounded logger |
| Phase 1 | API present; `BeaconFeatureFlags.manualReports()` exists; logging gated; **not** hardware-validated; reports do not age |
| Phases 2–10 | Designed; types/stubs; flags default off |
| Active intervention | Disabled. No motor/servo/network writes in this repository |
| Driver Station early safe-stop | Not implemented; public API study concludes it is not supported |
| Production safety claims | None |
| Releases | None |
| Default branch content | Initialize-only commit; Phase 0 lives on `scaffold/phase-0` |

Implementation phase: **research and scaffold**, not a robot-validated library.

---

## Implemented capabilities

Evidence: `src/main/java/org/allsparks/beacon/**`, tests under `src/test/java`, `docs/communications-health/**`.

- Immutable `LinkId`, `LinkState` (`UNKNOWN`, `HEALTHY`, `STALE`, `LOST`), `FailureDomain`, `LinkFailureReason`, `Freshness`, `Confidence`, `HealthReport`, `LinkHealth`.
- `BeaconClock` / `FakeClock` / `SystemNanoClock`.
- `HealthSource`, `FakeHealthSource`, `HealthRegistry` storing caller-supplied reports without probing.
- `BeaconSession.report` / `snapshot`; Phase 1 flag gates event logging only.
- `FreshnessPolicy` classification and Phase 0 `LinkState` mapping (tested in isolation).
- `CommandLease`, `RecoveryInhibit`, `NeutralControls` (types + unit tests; no actuator use).
- `RecoveryPolicy` (storage and backoff math; no execution).
- `SafeStateRequest` (request object; no dispatch).
- `PreflightFinding` / `PreflightStatus` (types; no inspector).
- `BeaconEventLogger` bounded ring buffer + CSV export.
- `BeaconFeatureFlags` with `isAnyInterventionEnabled()` covering phases 5, 6, 7, 9.
- `SystemCoreAdapterBoundary` hard-coded unavailable.
- Desktop example sketch (markdown, not a compiled sample).
- Source-backed research, driver-link feasibility, rules, architecture, phases, integration notes.
- CI: `./gradlew check` on Ubuntu and Windows; required-docs file presence job.
- Relative markdown link checker test.

---

## Documented but unimplemented capabilities

| Capability | Where documented | Code status | Phase gate |
|------------|------------------|-------------|------------|
| Time-aging of stored reports via `FreshnessPolicy` | `architecture.md` loop order | Policy unused by registry/source | Phase 1 seam |
| Loop-overhead histogram / Control Hub measurement | `phase-0-plan.md`, `testing.md`, issue #10 | `lastObserveDurationNanos` only | Phase 1 |
| Official DS telemetry formatter | `phase-0-plan.md` | Absent | Phase 1 |
| `PreflightInspector` | `preflight.md`, issue #15 | Types only | Phase 2 |
| Automatic health-transition / loop-timing events | issue #16 | Logger exists; no auto-record | Phase 3 |
| Advisory correlation | `fault-correlation.md`, issue #17 | Absent | Phase 4 |
| Shadow DS safe-state evaluation | `safe-state.md`, issue #18 | `SafeStateRequest.shadowOnly` type only | Phase 4 |
| Drivetrain safe-stop | `driver-link.md`, issues #20–21 | Blocked; flags exist | Phase 5 — **readiness gate unmet** |
| MIMIC safe-state contract execution | `mimic-integration.md`, issue #22 | Docs only | Phase 6 |
| Bounded ViDAR camera recovery | `recovery.md`, issue #23 | Policy type only | Phase 7 |
| Degraded-operation contracts | `degraded-operation.md`, issue #24 | Docs only | Phase 8 |
| Local collision guard | issue #25 | Absent | Phase 9 |
| Post-match log correlation | issue #26 | CSV header only | Phase 10 |
| SystemCore adapter | issue #27 | Boundary returns unavailable | Blocked on docs |
| ViDAR/MIMIC/AMPER/Pedro live DTO mapping | integration docs, issues #11–14 | Manual `HealthReport` only | Phase 1, sibling-dependent |
| Hardware measurements (stop latency, gamepad hold, Hub APIs) | `risks.md`, issues #3–5 | Not measured | Hardware |

---

## Architecture findings

| ID | Severity | Type | Finding |
|----|----------|------|---------|
| A1 | **HIGH** | ARCHITECTURE | `HealthRegistry` / `FakeHealthSource` do not apply `FreshnessPolicy`. `sample(long nowNanos)` returns the last assigned `LinkHealth` unchanged. Snapshots cannot become `STALE`/`LOST` from age. Evidence: `FakeHealthSource.java` lines 55–58; `HealthRegistry.java` `snapshot()`; no production call to `FreshnessPolicy.classify`. Tests never advance the clock and re-sample a stored healthy report. |
| A2 | **MEDIUM** | ARCHITECTURE | Consecutive success/failure counts are hard-coded to `1` on each `accept()`, not accumulated. Evidence: `FakeHealthSource.accept` lines 47–51. |
| A3 | **MEDIUM** | ARCHITECTURE | Later-phase types (`RecoveryPolicy`, `SafeStateRequest`, `PreflightFinding`, `CommandLease`, `RecoveryInhibit`) live in the Phase 0 public API. This is intentional vocabulary, but students can construct them and believe behavior exists. Mitigation today: README maturity table; no executor. |
| A4 | **LOW** | ARCHITECTURE | `BeaconSession.classifyMissing` returns the stored domain if the id exists, else `UNKNOWN`. Name suggests “missing” classification; it does not synthesize a missing-source report. Evidence: `BeaconSession.java` 104–109. Untested. |
| A5 | **LOW** | ARCHITECTURE | `HealthRegistry` exposes mutable `HealthSource` objects via `register`/`get`. Callers can `setHealth` on a `FakeHealthSource` and bypass `HealthReport`. Acceptable for tests; should stay out of student examples. |
| A6 | **INFORMATIONAL** | ARCHITECTURE | No compile-time dependency on ViDAR, AMPER, MIMIC, Pedro, TRACE, or HELM. Manual reports are the seam. Direction matches the Allsparks conceptual stack. |
| A7 | **INFORMATIONAL** | ARCHITECTURE | `HealthRegistry` uses unsynchronized `LinkedHashMap`. FTC OpMode loops are typically single-threaded. Not a current bug; document the assumption rather than adding locks speculatively. |
| A8 | **INFORMATIONAL** | ARCHITECTURE | No god-object hardware owner. `BeaconSession` is a façade over registry + logger + flags. Hidden global state was not found (no static mutable caches). |

No circular compile-time dependencies were found. Responsibility leakage is documentary only (integration guides describe sibling DTOs that do not exist in this repo).

---

## Correctness findings

| ID | Severity | Type | Finding |
|----|----------|------|---------|
| C1 | **HIGH** | CORRECTNESS | Same as A1: a `HEALTHY` report with an old timestamp still snapshots as `HEALTHY`. Examples use `sourceTimestampNanos = 0L` (`examples/README.md`), which `FreshnessPolicy.classify` treats as `UNKNOWN` if aging were applied. Current tests assert `HEALTHY` for timestamp `0` (`BeaconSessionTest.phase0StoresReportsWithoutLogging`). The tests encode “reporter state wins,” which conflicts with architecture’s “update freshness” loop step. |
| C2 | **MEDIUM** | CORRECTNESS | `FreshnessPolicy.toLinkState` maps `DELAYED` and `RECOVERING` to `HEALTHY`. Documented for Phase 0’s four-state enum. Students may treat delayed data as fully healthy. Acceptable until `LinkState` grows; must stay documented. Evidence: `FreshnessPolicy.java` 54–60; `architecture.md`. |
| C3 | **LOW** | CORRECTNESS | `CommandLease` allows a null command payload. Expiration still works; callers could apply null. |
| C4 | **LOW** | CORRECTNESS | `RecoveryPolicy.backoffNanos` left-shifts `initialBackoffNanos` by up to 8. Large initials can overflow to negative/zero without detection. No executor exists, so not a robot hazard today. |
| C5 | **LOW** | CORRECTNESS | `BeaconEvent.toCsvRow` does not escape newlines/CR in `detail`. Bounded in-memory logs; TRACE consumers could split rows. |
| C6 | **LOW** | CORRECTNESS | `FakeClock.advanceNanos` can overflow `long` or go backward if given a negative delta. Tests use small positive deltas. |
| C7 | **INFORMATIONAL** | CORRECTNESS | Future timestamps classify as `UNKNOWN`, not `HEALTHY`. Evidence: `FreshnessPolicyTest.futureTimestampIsUnknownNotHealthy`. Correct fail-safe. |
| C8 | **INFORMATIONAL** | CORRECTNESS | `Confidence.unknown()` uses NaN and `isKnown()==false`, not 0.0. Evidence: `LinkHealthTest.confidenceUnknownIsNotZero`. |

No evidence of timestamp unit mixing inside the library (nanos throughout; `Gamepad.timestamp` is documented as millis in research, not used in code). No overflow in freshness classification for documented test ranges.

---

## Safety findings

| ID | Severity | Type | Finding |
|----|----------|------|---------|
| S1 | **INFORMATIONAL** | SAFETY | No class writes motors, servos, USB, or network. Passive-by-construction for Phase 0. |
| S2 | **INFORMATIONAL** | SAFETY | `BeaconFeatureFlags` defaults: Phase 0 on, all later off. `isAnyInterventionEnabled()` is false unless phases 5, 6, 7, or 9 are enabled. Phase 8 (degraded operation) is **not** included in that method — correct if Phase 8 is consumer-side contracts only; confirm when Phase 8 is implemented. |
| S3 | **HIGH** | SAFETY | Phase 5 readiness gate is correctly **not** met. `driver-link.md` recommends no production DS safe-stop. Issues #20 and #21 are labeled `blocked`. Do not implement them. |
| S4 | **MEDIUM** | SAFETY | `RecoveryInhibit` defaults to `inhibited=true` until fresh + neutral observations. Safe default **if** callers honor it. Nothing in Phase 0 consults it before outputting commands because BEACON does not output commands. Later phases must not bypass this type. |
| S5 | **MEDIUM** | SAFETY | Replay/simulation: desktop tests cannot produce physical outputs. There is no adapter that could leak replay into hardware. Keep that boundary when TRACE exists. |
| S6 | **INFORMATIONAL** | SAFETY | Official watchdog and Hub keepalive are documented as non-circumventable. Library does not hook `EventLoopManager`. |
| S7 | **LOW** | SAFETY | Enabling Phase 1 logging does not change actuators, but unbounded `detail` strings plus 256 default logger capacity could add GC/telemetry pressure. Not measured on a Control Hub (issue #10). |

No path was found that could unexpectedly energize hardware from this library. Stop-condition for active-control work: **do not implement Phase 5–9 until listed gates pass.**

---

## Performance findings

All performance items are **predicted** unless noted.

| ID | Severity | Type | Finding |
|----|----------|------|---------|
| P1 | **MEDIUM** | PERFORMANCE | No Control Hub measurement of `BeaconSession.report` duration. `lastObserveDurationNanos` exists; issue #10 AC unchecked. Required before logging-heavy phases. |
| P2 | **LOW** | PERFORMANCE | `snapshot()` allocates a new `ArrayList` and new `LinkHealth` objects per call if sources rebuild health. Current `FakeHealthSource.sample` returns the same object (no alloc). If aging is added, each sample may allocate. Design for bounded, reuse-friendly snapshots in Phase 1. |
| P3 | **LOW** | PERFORMANCE | `exportCsv()` concatenates the full buffer on demand. Fine for post-match; do not call every loop. Untested as a hot-path prohibition. |
| P4 | **INFORMATIONAL** | PERFORMANCE | Logger capacity is caller-chosen; default session uses 256. Drops oldest; `droppedCount` tracked. Good bound. |
| P5 | **INFORMATIONAL** | PERFORMANCE | Desktop unit tests only. No allocation/GC benchmark. Create measurement issues rather than optimizing now. |

No measured Control Hub latency, CPU, or memory problem exists because no robot run was performed.

---

## API / usability findings

| ID | Severity | Type | Finding |
|----|----------|------|---------|
| U1 | **MEDIUM** | USABILITY | First-use path is desktop `./gradlew test` plus markdown examples. There is no compiled example module and no TeamCode OpMode in-tree (intentional: no FTC SDK on the compile classpath). Beginners must copy-paste. |
| U2 | **MEDIUM** | USABILITY | `examples/README.md` uses timestamp `0L` for “healthy” reports. That teaches a bad habit if freshness aging is added. |
| U3 | **LOW** | USABILITY | Two `report` overloads (`HealthReport` vs `String id, HealthReport`) plus registry `report(String, FailureDomain, HealthReport)` are easy to confuse. |
| U4 | **LOW** | DOCUMENTATION | Advanced types are importable in Phase 0. Progressive disclosure is flag-based, not classpath-based. Acceptable if README “Current status” stays accurate. |
| U5 | **INFORMATIONAL** | USABILITY | Naming (`LinkHealth`, `FailureDomain`, `Confidence.unknown()`) matches the teaching goal. Glossary exists. |
| U6 | **INFORMATIONAL** | USABILITY | Disable path: omit `BeaconSession` or use defaults. Removing BEACON is deleting `report(...)` calls, as designed. |

---

## Testing findings

| ID | Severity | Type | Finding |
|----|----------|------|---------|
| T1 | **HIGH** | TESTING | Missing behavioral test: store healthy report, advance `FakeClock` past lost threshold, `snapshot()` still healthy. This is the regression test for A1/C1. |
| T2 | **MEDIUM** | TESTING | No test that consecutive counts increment across repeated successes/failures. |
| T3 | **MEDIUM** | TESTING | CI does not compile against the FTC SDK. Android/FTC compatibility is untested. Desktop Java 11 source compiled with JDK 17. |
| T4 | **LOW** | TESTING | `DocLinkCheckerTest` walks all `.md` files including this audit; keep relative links valid. HTTP links are not fetched. |
| T5 | **LOW** | TESTING | `classifyMissing`, CSV newline, `RecoveryPolicy` overflow, and `BeaconSession.report` id-mismatch are untested. |
| T6 | **INFORMATIONAL** | TESTING | Tests are not flaky by inspection (deterministic `FakeClock`). None disabled. They run in CI on PR to `main`. |
| T7 | **INFORMATIONAL** | TESTING | Tests do not claim hardware coverage. `FreshnessSimulationTest` is correctly desktop-only. |

CI evidence: workflow run `32059633955` on PR #28 completed **success** (2026-08-17). This audit did not re-run that GitHub job; local `./gradlew check` is recorded in the validation section when executed during orchestrator work.

---

## Documentation findings

Documentation is unusually complete for a 0.1 scaffold and generally matches the code. Defects:

| ID | Severity | Type | Finding |
|----|----------|------|---------|
| D1 | **MEDIUM** | DOCUMENTATION | Architecture describes “Update freshness” in the loop order, but code does not update freshness. Readers may believe aging exists. |
| D2 | **MEDIUM** | DOCUMENTATION | Phase 0 status in `phases.md` says “implemented in this scaffold” while several Phase 0 GitHub issues still have hardware ACs open. Software vs hardware completion is not distinguished on the issues themselves. |
| D3 | **LOW** | DOCUMENTATION | No `docs/audits/` existed before this file. No in-repo priority ledger existed. |
| D4 | **LOW** | DOCUMENTATION | `SECURITY.md` says report via advisories “when available” or “private email contact published by the maintainers” — no email is published. |
| D5 | **INFORMATIONAL** | DOCUMENTATION | Research classifications (verified fact vs inference vs hypothesis) are consistently applied. Driver-link study is honest about unmeasured hardware. |
| D6 | **INFORMATIONAL** | DOCUMENTATION | Issue bodies use the phase-work template, not the full orchestrator schema (Problem / Evidence / Acceptance / Blocked by / Rollback, etc.). |

---

## Dependency findings

| ID | Severity | Type | Finding |
|----|----------|------|---------|
| X1 | **MEDIUM** | SECURITY | CI uses `actions/checkout@v4` and `actions/setup-java@v4` (moving tags), not commit SHAs. Dependabot watches github-actions monthly. |
| X2 | **LOW** | COMPATIBILITY | Runtime dependencies: none besides the JDK. Test: JUnit 5.10.2 BOM. No FTC SDK. Low supply-chain surface; also no compile check that TeamCode can import the library. |
| X3 | **INFORMATIONAL** | SECURITY | Gradle wrapper 8.7; `gradle-wrapper.jar` present; `validateDistributionUrl=true`. License MIT; JUnit EPL — typical and compatible for tests. |
| X4 | **INFORMATIONAL** | SECURITY | No secrets in repo from inspection (`.gitignore` covers `.env`, `local.properties`). `SECURITY.md` exists. |
| X5 | **INFORMATIONAL** | COMPATIBILITY | No Maven publish config yet. Version is snapshot. Expected for Phase 0. |

Do not perform a broad dependency upgrade in this cycle.

---

## Repository-health findings

| ID | Severity | Type | Finding |
|----|----------|------|---------|
| R1 | **HIGH** | SECURITY | `main` is **not** branch-protected. Rulesets empty. Required checks are not enforced. Admin can merge without review. |
| R2 | **HIGH** | INTEGRATION | Draft PR #28 contains the entire Phase 0 product. `main` cannot be used as a library until it merges. 27 issues remain open; several software ACs are already checked in the issue text. |
| R3 | **MEDIUM** | USABILITY | Default GitHub labels plus a rich phase/domain set exist. Severity and finding-type labels did not exist at audit start. No milestones are closed. No releases. |
| R4 | **MEDIUM** | DOCUMENTATION | No GitHub Project was readable (`gh project list` failed: token missing `read:project`). Priority ledger is therefore a repository document. |
| R5 | **LOW** | TESTING | CI `docs-structure` job checks a subset of files; it will not fail if architecture drifts from code. |
| R6 | **LOW** | USABILITY | `deleteBranchOnMerge` is false. Merge methods: merge commit, squash, and rebase all allowed. |
| R7 | **INFORMATIONAL** | DOCUMENTATION | Issue templates (bug, feature, phase work) and PR template exist and include safety checkboxes. Dependabot enabled. CODE_OF_CONDUCT, CONTRIBUTING, LICENSE present. No `AGENTS.md`. |
| R8 | **INFORMATIONAL** | INTEGRATION | Open PRs: #28 draft. Merged PRs: none. Failed CI: none in the last 15 runs (only the one successful PR run). |

---

## Cross-project integration findings

Conceptual direction: ViDAR / Pedro / AMPER / MIMIC / BEACON → TRACE → HELM.

| ID | Severity | Type | Finding |
|----|----------|------|---------|
| I1 | **MEDIUM** | INTEGRATION | Issues #11–14 depend on sibling health DTOs “once available.” BEACON correctly uses `HealthReport` as the contract. Do not add compile-time sibling dependencies. |
| I2 | **MEDIUM** | INTEGRATION | TRACE-compatible CSV header is implemented; TRACE itself is not a dependency. Schema evolution is unversioned (`timestampNanos,type,linkId,domain,detail`). |
| I3 | **LOW** | INTEGRATION | AMPER electrical vs communications split is documented; no code enforces that electrical reports use `FailureDomain.ELECTRICAL`. |
| I4 | **INFORMATIONAL** | INTEGRATION | HELM is not referenced in code. Correct: BEACON must not select tasks. |
| I5 | **INFORMATIONAL** | INTEGRATION | Other Allsparks repositories were not modified. No circular dependency can exist yet because there are no published artifacts. |

---

## Readiness assessment

**Safe to continue development:** yes, on the passive path.

**Safe to enable active features:** no.

**Safe to merge Phase 0 after human review:** yes, provided PR #28 review confirms no hardware writes (audit found none) and docs keep “not hardware-validated” visible.

**Phase 1 ready after PR #28 merge:** software seam (freshness aging + consecutive counts + tests) is ready and does not need hardware. Overhead measurement and sibling DTO mapping are not ready.

**Phase 5 ready:** no. Public DS freshness API unproven (issue #2). Hardware stop-latency unmeasured (issue #3).

**Release goal:** not reached. No 0.1.0 non-snapshot release should claim robot safety.

---

## Recommended work order

1. Human review and merge of PR #28 (Phase 0 scaffold). Orchestrator must not auto-merge.
2. Apply `FreshnessPolicy` when sampling stored health; accumulate consecutive counts; fix example timestamps; add clock-advance tests. Unblocks honest Phase 1 observation.
3. Enable branch protection / required CI on `main` (human policy decision).
4. Pin GitHub Actions to SHAs.
5. Keep Phase 1 sibling report issues waiting on DTOs; ship BEACON-side examples that stay manual.
6. Preflight inspector (Phase 2) only after registry snapshots are time-honest.
7. Event transitions (Phase 3) only after overhead is known or explicitly bounded.
8. Shadow safe-state (Phase 4) after Phase 3.
9. Hardware measurement issues (#3, #4, remaining #2/#5/#10) when restrained hardware and an operator are available.
10. Phase 5+ remains blocked.

Deferred/rejected:

- Production DS safe-stop on `Gamepad.timestamp` or loop iteration — **rejected** by `driver-link.md`.
- Importing AdvantageKit/WPILib/PurpleLib — **rejected** by `build-vs-adopt.md`.
- SystemCore recovery — **deferred** until authoritative docs (issue #27).
- Broad dependency upgrades — **deferred**.
- Thread-safety locks on the registry — **deferred** until a second thread is introduced.
- Cosmetic cleanup unrelated to the above — **rejected** for this cycle.

---

## Evidence and references

- Repository: https://github.com/The-Allsparks/BEACON
- Audited tree: commit `984f47a69e7c50f4bb9724fb5d626ddaab6201e4` on `scaffold/phase-0`
- PR: https://github.com/The-Allsparks/BEACON/pull/28
- CI run: `32059633955` (success)
- Primary code: `BeaconSession.java`, `HealthRegistry.java`, `FakeHealthSource.java`, `FreshnessPolicy.java`, `BeaconFeatureFlags.java`
- Primary docs: `README.md`, `docs/communications-health/architecture.md`, `phases.md`, `driver-link.md`, `research.md`, `phase-0-plan.md`
- Issues at audit time: #1–#27 open; this cycle added #29 (roadmap), #30 (freshness sampling), #31 (Actions SHA pins), #32 (branch protection)
- Identity: `gh api user` → `TA-C-GHill`; collaborator role admin
- Branch protection API: HTTP 404 “Branch not protected”; rulesets `[]`

---

## Severity and type definitions

**Severity**

- `BLOCKER`: Prevents safe or meaningful continued development.
- `CRITICAL`: Could cause unsafe robot behavior, corrupt fundamental results, or invalidate the architecture.
- `HIGH`: Materially affects reliability, integration, or major project goals.
- `MEDIUM`: Important but does not block the next safe vertical slice.
- `LOW`: Local improvement, cleanup, or minor usability problem.
- `INFORMATIONAL`: Observation requiring no current action.

**Type:** `SAFETY`, `CORRECTNESS`, `ARCHITECTURE`, `PERFORMANCE`, `TESTING`, `DOCUMENTATION`, `SECURITY`, `COMPATIBILITY`, `USABILITY`, `INTEGRATION`, `RESEARCH`.
