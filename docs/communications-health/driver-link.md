# Driver Station freshness feasibility

**Recommendation:** do **not** implement a production Driver Station communication safe-stop in this library until a supported public freshness signal is proven. Limit Phase 5 to research, shadow-mode analysis of *official* stop behavior, and local hazard protection that does not claim DS-packet freshness.

Access date: 2026-08-17.

## Question

Can an external FTC library obtain a reliable indication of **fresh Driver Station communication** using supported public APIs?

## Do not assume

- an OpMode loop iteration means a fresh Driver Station packet arrived;
- an unchanged gamepad value is fresh;
- `Gamepad.timestamp` is a network heartbeat;
- a telemetry update proves control-packet freshness;
- a private or internal SDK API is stable or appropriate for a library.

## Supported public options

| Option | What OpMode code can see | Freshness meaning | False-positive risk |
|--------|--------------------------|-------------------|---------------------|
| `gamepad1` / `gamepad2` fields | Last delivered gamepad state | Input values, not packet times | High if used as heartbeat |
| `Gamepad.timestamp` / `setTimestamp` | Time the gamepad **state last changed**, `uptimeMillis` base (RobotCore 11.2.0) | Input-event clock | High: held controls may not change; change does not prove Wi-Fi |
| `opModeIsActive()` / `isStopRequested()` | Official stop, including after DS disconnect timeout | After-the-fact: the SDK already decided to stop | Cannot provide *early* stop; using it to “detect loss” races the official watchdog |
| `LynxModule.isNotResponding()` | Hub module path | Expansion/Control Hub I/O, not DS Wi-Fi | Wrong domain if used as DS loss |
| `HardwareDeviceHealth.getHealthStatus()` | Device HEALTHY/UNHEALTHY/UNKNOWN/CLOSED | Local device | Wrong domain |
| Official DS ping UI | Visible to drivers, not to OpMode | Network RTT at the app layer | Unavailable to library code |
| Hub LED keepalive | Visible on hardware | Firmware fail-safe | Unavailable to library code |

## Internal / private options

| Option | Surface | Stability risk | Rules / safety |
|--------|---------|----------------|----------------|
| `EventLoopManager.getHeartbeat()` | Public method on an internal manager OpModes do not receive | High: not a supported team API | Reaching it via internals is inappropriate for competition libraries |
| `heartbeatEvent` / `onPeerConnected` / `onPeerDisconnected` | Same manager; `EventLoopMonitor` | High | Could observe peer status, but hooking the event loop can interfere with official behavior |
| `RecvLoopRunnable` / Robocol internals | Internal network | Unacceptable | Must not inspect or inject packets |
| Reflection into RC services | Private | Unacceptable | Must not ship |

## Test results

**Measured result:** none on robot hardware in this commit.

Desktop simulation (unit tests) only shows that:

- a freshness policy can ignore loop iterations that do not carry a source timestamp;
- a held non-neutral stick does not clear recovery inhibit;
- ordinary 15 ms jitter stays `CURRENT` under a 40 ms delayed threshold.

Those tests do **not** prove DS packet timing.

## Expected official detection latency

**Observed implementation (not re-measured):** FIRST troubleshooting describes heartbeats about every 0.1 s and a Driver Station disconnect after more than **2 seconds** without communication, at which point the Robot Controller stops the OpMode.

**Engineering inference:** a BEACON “early” safe-stop that claimed sub-second DS-loss detection would be claiming a signal the public OpMode API does not provide. Any production stop faster than the official watchdog would require an unsupported hook.

**FTA-observed implementation:** drivetrain motors may remain powered until the controller notices loss; community reports describe seconds of continued motion. That delay is owned by the official stack. BEACON must not weaken it.

## False-positive risk of naive detectors

| Naive detector | Failure mode |
|----------------|--------------|
| “Stick still at +1.0” | Stale held command looks healthy |
| “Timestamp unchanged for 100 ms” | Held analog may not generate events; stuttering if used to zero drive |
| “Loop still running” | OpMode thread can run after packets stop, until official timeout |
| “Telemetry sent” | Outbound telemetry is not inbound control freshness |
| “No gamepad motion in autonomous” | Legitimate auto has no gamepad motion |

## Rules implications

Using public telemetry and in-memory reports is compatible with R704 D. Sniffing Wi-Fi, injecting Robocol, or attaching a second DS-like client is prohibited (E301, E302, R704, R904). See [rules.md](rules.md).

## Recommendation

1. **Phase 0–4:** observe, explain, warn, correlate, and evaluate safe-state in **shadow mode** only.
2. **Do not ship** a production DS safe-stop based on fabricated freshness.
3. **If** FIRST later exposes a supported `isPeerConnected()` or heartbeat age to OpModes, revisit Phase 5 with measurements.
4. Until then, the official watchdog remains the DS-loss actuator. BEACON may log `isStopRequested()` as an official-stop observation without racing it.

Teach: **a running loop is not a fresh command.**
