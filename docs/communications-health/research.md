# Research

Access date for all live URLs: **2026-08-17**, unless a row says otherwise.

Classification key: **verified fact**, **observed implementation**, **measured result**, **engineering inference**, **untested hypothesis**, **unavailable through public API**, **future-hardware possibility**.

This report is source-grounded. Where a claim required reading SDK internals that are not part of the supported OpMode surface, it is labeled **observed implementation** or **unavailable through public API**.

## 1. Current FTC SDK communication behavior

The public SDK repository is [FIRST-Tech-Challenge/FtcRobotController](https://github.com/FIRST-Tech-Challenge/FtcRobotController). RobotCore **11.2.0** Javadocs are published at [javadoc.io](https://javadoc.io/doc/org.firstinspires.ftc/RobotCore/11.2.0).

**Verified fact:** `EventLoopManager` receives Robocol datagrams and dispatches `heartbeatEvent`, `gamepadEvent`, `onPeerConnected`, and `onPeerDisconnected`. `getHeartbeat()` returns the current heartbeat object.

**Unavailable through public API:** ordinary OpMode code is not given an `EventLoopManager` or a documented `isDriverStationConnected()` method analogous to WPILib `DriverStation.isDSAttached()`.

**Observed implementation:** official troubleshooting states that the Driver Station sends heartbeat packets about every tenth of a second, and that more than two seconds without communication is treated as a disconnect that stops the OpMode for safety ([FtcRobotController wiki Troubleshooting](https://github.com/FIRST-Tech-Challenge/FtcRobotController/wiki/Troubleshooting)).

## 2. Driver Station and Robot Controller heartbeat

**Verified fact:** `com.qualcomm.robotcore.robocol.Heartbeat` exists in RobotCore 6.2.1 Javadocs (and remains on `EventLoopManager.getHeartbeat()` in 11.2.0). It is a Robocol message “used to know if the connection between the client/server is still alive.”

**Unavailable through public API:** OpModes do not receive that object.

**Engineering inference:** a library that reached `EventLoopManager` via internal references would be using an unstable, unsupported surface. See [driver-link.md](driver-link.md).

## 3. Gamepad packet and timestamp behavior

**Verified fact (RobotCore 11.2.0):** `Gamepad.setTimestamp(long)` “Sets the time at which this Gamepad last changed its state, in the uptimeMillis time base.” `refreshTimestamp()` “Refreshes the Gamepad's timestamp to be the current time.”

**Engineering inference:** `Gamepad.timestamp` is an input-state clock, not a network heartbeat. A held stick may stop generating change events on some hosts, so an unchanged timestamp is not proof of Driver Station loss, and an updating timestamp is not proof of a fresh Wi-Fi packet.

**Untested hypothesis:** whether the Driver Station transmits gamepad Robocol messages continuously while controls are held steady. This must be measured on hardware; it is not assumed.

## 4. OpMode stop and auto-stop

**Observed implementation:** the Robot Controller stops the running OpMode when Driver Station communication is lost for the official disconnect interval (wiki: over two seconds). `LinearOpMode.opModeIsActive()` / `isStopRequested()` then become the OpMode-visible consequence.

**Engineering inference:** an OpMode loop iteration after stop has been requested is not a fresh Driver Station packet. An OpMode loop iteration *before* stop is requested also does not prove a packet arrived in that iteration.

## 5. REV Control Hub keepalive

**Verified fact:** REV LED documentation states that a blinking blue Hub LED means “Keep alive has timed out. Fault will clear when communication resumes.” Orange blink is battery below 7 V and is **not** overwritten by the keepalive pattern ([REV LED blink codes](https://docs.revrobotics.com/duo-control/troubleshooting-the-control-system/led-blink-codes.md)).

**Verified fact:** REV Control Hub documentation lists “Failsafe Mode at Communication Loss” as a hardware/firmware behavior ([REV-31-1595 user manual](https://revrobotics.ca/content/docs/REV-31-1595-UM.pdf)).

**Unavailable through public API:** OpMode code cannot read the Hub LED or the firmware keepalive timer.

## 6. Control Hub to Expansion Hub

**Verified fact:** `hardwareMap.getAll(LynxModule.class)` is the documented way to obtain Hub objects ([game manual 0 lynx-module notes](https://gm0.org/en/latest/docs/software/adv-control-system/lynx-module.html); treat gm0 as secondary).

**Observed implementation:** `LynxModule.isNotResponding()` exists on the Hub object in published SDK Javadocs / OpenRC source. Command timeouts of 250 ms appear in RobotCore logs when a module does not ACK ([historical ftc_app issue 674](https://github.com/ftctechnh/ftc_app/issues/674)).

**Engineering inference:** Expansion Hub loss is a **bus/module** failure domain, not a Driver Station network failure. RS-485 cable replacement has been a documented fix for retry storms.

## 7. Module disconnect and USB/FTDI recovery

**Observed implementation:** the SDK tracks module unresponsiveness (`noteNotResponding`, `LynxModuleWarningManager.reportModuleUnresponsive`) and can re-engage modules. Automatic USB recovery after ESD is a firmware/OS concern (REV notes improved USB recovery after fault events in Hub firmware history).

**Engineering inference:** BEACON should not duplicate Hub reconnection. It should record `isNotResponding()` when an adapter exists (later phase) and leave recovery to the SDK unless a bounded, low-risk policy is explicitly enabled.

## 8. I²C heartbeat and health APIs

**Verified fact:** `I2cDeviceSynch` exposes `setHeartbeatInterval`, `setHeartbeatAction`, and health-status setters/getters in published Javadocs. Default heartbeat interval is zero (no heartbeat).

**Verified fact:** `HardwareDeviceHealth.HealthStatus` in RobotCore 11.2.0 is `UNKNOWN`, `HEALTHY`, `UNHEALTHY`, `CLOSED`.

**Engineering inference:** I²C health is per-device and is not a Wi-Fi diagnostic.

## 9. UVC camera lifecycle

**Verified fact:** FTC docs warn that an ESD event on the Control Hub **USB 2.0** port can affect the Wi-Fi chip and cause Driver Hub disconnects; cameras should use **USB 3.0** ([ftc-docs Control Hub ports](https://ftc-docs.firstinspires.org/en/latest/control_hard_compon/rc_components/hub/ports/ch-ports.html); [REV general troubleshooting](https://docs.revrobotics.com/duo-control/troubleshooting-the-control-system/troubleshooting-the-control-system.md)).

**Engineering inference:** a camera USB reset can present as a “network” failure. ViDAR should report camera/pipeline health; BEACON should not treat a missing frame as Driver Station loss.

## 10. Driver Hub gamepad reconnection

**Verified fact:** FTC SDK release notes describe auto-recovery of dropped gamepads when unique serial numbers are available, with an explicit exception for Etpark pads. Start+A/B assignment remains the user-visible binding method.

**Verified fact:** REV documents a Driver Hub Wi-Fi-after-sleep defect; leaving the screen on and the DS app open is the mitigation ([Driver Hub troubleshooting](https://docs.revrobotics.com/duo-control/troubleshooting-the-control-system/driver-hub-troubleshooting.md)).

## 11. REV logs

**Verified fact:** REV Hardware Client includes a [Log Viewer](https://docs.revrobotics.com/rev-hardware-client/duo/log-viewer). Robot Controller logs (`robotControllerLog.txt`) and Wi-Fi/updater logs are post-match artifacts, not OpMode APIs.

## 12. Official FTC communications rules

See [rules.md](rules.md). Primary manual: BIOBUZZ™ Competition Manual V0, 31 Jul 2026. Key rules: **E301, E302, R704, R706, R711, R904**.

**Verified fact:** BEACON must not create extra wireless systems, interfere with other networks, stream unauthorized data, or modify official control packets.

## 13. FRC Driver Station logs and WPILib

**Verified fact:** FRC DS log viewer overlays voltage, trip time, lost packets, CPU%, and robot mode ([WPILib DS log viewer](https://docs.wpilib.org/en/stable/docs/software/driverstation/driver-station-log-viewer.html)).

**Verified fact:** WPILib exposes `DriverStation.isDSAttached()` and a `Watchdog` class for loop overruns ([WPILib 2026.2.2 Javadoc](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/wpilibj/DriverStation.html)). These APIs **do not run on the FTC Control Hub**.

**Engineering inference:** FRC patterns (correlate voltage with disconnects; do not resume stale commands; log inputs) transfer. FRC classes do not.

## 14. AdvantageKit and PurpleLib

**Verified fact:** AdvantageKit records all inputs for deterministic replay ([docs.advantagekit.org](https://docs.advantagekit.org/getting-started/what-is-advantagekit/)). Templates include dashboard alerts for disconnected devices.

**Verified fact:** PurpleLib (LASA Robotics) is an FRC hardware-wrapper library with AdvantageKit logging, Spark fault monitoring, and “health monitoring and automatic recovery” ([GitHub](https://github.com/lasarobotics/PurpleLib/)).

**Engineering inference:** BEACON can adopt the *idea* of IO-layer health and replay-friendly event logs without importing WPILib. Automatic CAN recovery does not map onto REV Hubs.

## 15. Public FTC communication-health implementations

No maintained FTC-first library was found that provides a modular, phased, feature-flagged communications-health layer matching BEACON’s scope (manual registry + preflight + correlation + lease + inhibit). Team OpModes sometimes poll `LynxModule.isNotResponding()` or camera frame timestamps. Those are local patterns, not a reusable framework.

**Engineering inference:** a standalone BEACON library is justified for The Allsparks ecosystem. See [build-vs-adopt.md](build-vs-adopt.md).

## 16. SystemCore

**Future-hardware possibility:** REV SystemCore may eventually expose richer communication or health APIs. **No authoritative SystemCore communications documentation was located** for this scaffold. `SystemCoreAdapterBoundary.isAvailable()` returns false. Do not implement SystemCore-specific recovery.

## 17. Official watchdog delay (expected, not newly measured)

| Claim | Class | Notes |
|-------|-------|-------|
| Heartbeats about every 0.1 s | observed implementation | FTC wiki Troubleshooting |
| DS disconnect after >2 s without communication | observed implementation | same wiki; **not** re-measured on Allsparks hardware in this commit |
| Hub keepalive fail-safe (blinking blue) | verified fact | REV LED docs; timeout duration not published as an OpMode constant |
| Motors may continue until the controller notices loss | observed implementation | FTC community FTA comments; delay is seconds-scale, not loop-scale |
| Early (<2 s) DS loss detection from OpMode APIs | unavailable through public API | see [driver-link.md](driver-link.md) |

**Measured result:** none in this repository. Desktop unit tests measure algorithm timing only. Robot measurement of official stop latency is issue-tracked and must use restrained hardware.
