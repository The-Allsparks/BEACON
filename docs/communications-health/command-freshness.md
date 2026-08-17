# Command freshness

Teleop commands are time-limited **leases**. A valid lease requires evidence that the original command source remains fresh.

## Conceptual API

`CommandLease<T>` stores the command, source timestamp, expiration timestamp, and `CommandSource`. `isFresh(nowNanos)` is true only when `nowNanos <= expiration`.

This library implements that API as a Java 11 immutable class.

## Incorrect renewal

```java
// Incorrect if gamepad data may be stale.
lease.refresh(gamepad1.left_stick_y);
```

The OpMode loop continuing, or a stick remaining at a previous value, is **not** a renew signal.

## On expiration

- invalidate the command;
- enter the configured safe state **only if that phase is enabled and freshness is verified**;
- log the event;
- do not restore the command automatically.

## Autonomous versus teleop

Do not stop legitimate autonomous execution merely because no gamepad movement is present. Always respect official FTC enable, disable, and OpMode-stop. Do not continue or initiate autonomous driving to conceal a lost teleop connection.

## Recovery inhibit

When communication returns, require:

- multiple consecutive fresh observations;
- stable connection for a configurable duration;
- neutral sticks and triggers;
- acceptable AMPER and MIMIC state;
- no critical BEACON fault;
- optional deliberate re-arm.

Do not resume previous drivetrain power, queued intake actions, interrupted elevator transitions, driver-assist motion, or old autonomous-assist commands. Each subsystem must declare whether an interrupted operation may resume.

Phase 0 implements `RecoveryInhibit` and `NeutralControls` as pure functions with no actuator writes.
