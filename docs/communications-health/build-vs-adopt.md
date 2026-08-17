# Build versus adopt

## Decision

**Build a standalone BEACON library** for The Allsparks, with a narrow FTC-first scope. Do not import AdvantageKit, PurpleLib, or WPILib. Do not duplicate FTC SDK watchdog, Hub keepalive, or module reconnection.

No maintained FTC-compatible project was found that already provides modular, phased, feature-flagged communications-health with a manual registry, command leases, and recovery inhibit.

## 1. What does the official FTC watchdog already stop?

When Driver Station communication is lost beyond the official interval (documented as more than about two seconds without communication), the Robot Controller **stops the OpMode**. Hub firmware separately enters keepalive-timeout fail-safe (blinking blue LED; motors/servos to fail-safe).

## 2. What is the observed delay?

**Not measured on Allsparks hardware in this commit.** Documented DS-side timeout is on the order of **2 seconds**. Heartbeats are described as ~10 Hz. Hub keepalive timeout is not published as an OpMode constant. Community FTA comments describe motors continuing until the controller notices loss.

## 3. What modules already reconnect automatically?

The FTC SDK tracks Lynx module unresponsiveness and re-engagement. USB device re-enumeration after ESD is firmware/OS behavior. Driver Station gamepad auto-recovery exists when unique serials are available (not Etpark). **Do not duplicate these.**

## 4. Which recovery should not be duplicated?

- Official OpMode stop on DS loss
- Hub keepalive fail-safe
- Lynx module reconnect / USB recovery
- Gamepad rebinding implemented by the DS app
- Channel selection performed by event staff
- AMPER electrical limiting (when enabled there)
- MIMIC mechanism holding/ratchet logic
- ViDAR camera pipeline restart until Phase 7 explicitly delegates a bounded policy

## 5. Which health signals are exposed to OpMode code?

- Last gamepad state and `Gamepad.timestamp` (input-event clock)
- `opModeIsActive` / `isStopRequested`
- `LynxModule.isNotResponding` and bulk-read success/failure
- `HardwareDeviceHealth` / I²C synch health
- Camera frame callbacks / VisionPortal status (when using those APIs)
- Voltage sensors (AMPER’s domain)
- Loop timing measured in user code

## 6. Which exist only in system logs or UI?

- Robocol heartbeat objects
- Peer connected/disconnected events
- DS ping graph
- Hub LED keepalive / battery codes
- REV Wi-Fi and updater logs
- RC `robotControllerLog.txt` disconnect reasons
- Whether interference is malicious

## 7. Can AdvantageKit patterns be adapted without importing an FRC framework?

Yes, conceptually:

- log inputs, not just outputs;
- health at the IO boundary;
- replay-friendly event records;
- disconnected-device alerts.

No, as a dependency: AdvantageKit and PurpleLib require WPILib/FRC vendors.

A future **TRACE** logger should own the on-disk format. BEACON’s `BeaconEventLogger` CSV is a compatible starting point.

## 8. Is a standalone BEACON library justified?

**Yes**, as a coordination and vocabulary layer:

- AMPER already owns electrical health;
- MIMIC already owns mechanism safe states;
- ViDAR already owns camera/pipeline health;
- Pedro already owns localization health;
- FTC already owns the network protocol and watchdog.

BEACON owns **trust**: which reports are fresh, how they correlate, and (later) whether to *request* safe state. That job is not served by copying FRC libraries onto a Control Hub.

## 9. Which capabilities belong in a future shared TRACE logger?

- Rolling export format shared by AMPER, MIMIC, ViDAR, Pedro, BEACON
- Post-match merge with RC/REV logs
- Desktop timeline UI

Runtime safety must not depend on TRACE.

## 10. What must wait for SystemCore?

Any claim about new Hub health, keepalive, or DS APIs. Authoritative SystemCore communications documentation was **not** available. Treat as **future-hardware possibility** only.

## Delegation map

| Behavior | Delegate to |
|----------|-------------|
| Official DS/RC protocol, enable/disable, OpMode stop | FTC SDK |
| Keepalive fail-safe, LED codes, USB recovery, channel firmware | REV firmware / OS |
| Camera frames and pipeline stalls | ViDAR |
| Mechanism holding, ratchet, profile cancel | MIMIC |
| Voltage sag vs comms-looking failures | AMPER |
| Pose / localization trust | Pedro |
| Freshness vocabulary, registry, correlation, leases, inhibit, preflight | BEACON |
| Post-match multi-log story | TRACE (future) + Phase 10 tooling |
