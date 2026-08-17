# Student exercises

Use simulated events in unit tests or recorded CSV. No unrestrained robot.

## 1. What does connected mean?

Given a loop that still runs and a stick held at `y = -1.0` with `lastValidTimestamp` 300 ms ago, classify freshness with `FreshnessPolicy.ofMillis(40, 80, 250)`. Write why this is not “the driver is still commanding forward.”

## 2. Name the domain

Match each symptom to a domain in [failure-domains.md](failure-domains.md):

- Expansion Hub LED blinking blue, DS still connected
- Camera frames stop; DS ping stays low
- Voltage collapses; then DS drops
- Gamepad unassigned; robot still enabled
- Loop time 200 ms; all devices healthy

## 3. Insufficient evidence

Write an advisory that lists three simultaneous failures and ends with `INSUFFICIENT_EVIDENCE` instead of “we were jammed.”

## 4. Lease expiration

Using `CommandLease`, show that advancing a `FakeClock` expires a drive command even if you keep passing the same stick value into robot code.

## 5. Recovery inhibit

Simulate communication returning with the stick still forward. Confirm `RecoveryInhibit` stays active. Repeat with neutral sticks until it clears.

## 6. Preflight optional vs required

Design two findings for a missing Expansion Hub: required vs optional. Which status is `READY_DEGRADED`?

## 7. Rules red team

Which of these are illegal during a match: extra laptop on the robot Wi-Fi, FTC Dashboard stream, Wi-Fi analyzer packet capture of another team, official DS telemetry of BEACON states? Cite [rules.md](rules.md).
