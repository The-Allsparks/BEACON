# References

Access date: **2026-08-17** unless noted. Prefer these official URLs over mirrors.

| Title | Org / author | URL | Date / version | Applies to FTC? | Supported claim | Limitations |
|-------|--------------|-----|----------------|-----------------|-----------------|-------------|
| BIOBUZZ™ Competition Manual HTML | FIRST | https://ftc-resources.firstinspires.org/ftc/game/manual | V0, updated 31 Jul 2026 | Yes | E301, E302, R704, R706, R711, R904 wireless/control rules | Pre-kickoff V0; re-check after Team Updates |
| Current game materials | FIRST | https://ftc-resources.firstinspires.org/ftc/game | 2026–2027 BIOBUZZ | Yes | Identifies official manual location | Translations marked unofficial |
| DECODE archive manual | FIRST | https://ftc-resources.firstinspires.org/ftc/archive/2026/game/manual | 2025–2026 TU32 | Historical | Same wireless rule family | Prior season |
| FtcRobotController | FIRST | https://github.com/FIRST-Tech-Challenge/FtcRobotController | current `master` | Yes | Public SDK entrypoint | RobotCore sources not fully in this repo |
| RobotCore Javadocs | FIRST | https://javadoc.io/doc/org.firstinspires.ftc/RobotCore/11.2.0 | 11.2.0 | Yes | Gamepad timestamp; EventLoopManager heartbeat/peer APIs; HardwareDeviceHealth | Javadoc ≠ supported OpMode surface |
| Gamepad | FIRST | https://javadoc.io/static/org.firstinspires.ftc/RobotCore/11.2.0/com/qualcomm/robotcore/hardware/Gamepad.html | 11.2.0 | Yes | Timestamp is last **state change** in uptimeMillis | Not a network heartbeat |
| EventLoopManager | FIRST | https://javadoc.io/static/org.firstinspires.ftc/RobotCore/11.2.0/com/qualcomm/robotcore/eventloop/EventLoopManager.html | 11.2.0 | Internal | getHeartbeat, onPeerConnected/Disconnected | Unavailable to ordinary OpModes |
| HardwareDeviceHealth.HealthStatus | FIRST | https://javadoc.io/static/org.firstinspires.ftc/RobotCore/11.2.0/com/qualcomm/robotcore/hardware/HardwareDeviceHealth.HealthStatus.html | 11.2.0 | Yes | UNKNOWN/HEALTHY/UNHEALTHY/CLOSED | Unspecified UNHEALTHY cause |
| FTC SDK Troubleshooting wiki | FIRST | https://github.com/FIRST-Tech-Challenge/FtcRobotController/wiki/Troubleshooting | accessed 2026-08-17 | Yes | Heartbeats ~0.1 s; DS disconnect >2 s stops OpMode; ping UI guidance | Not a robot measurement |
| Robot Troubleshooting Guide | FIRST | https://ftc-resources.firstinspires.org/ftc/team/robot-troubleshooting | Rev 25-26.1 | Yes | DS disconnect causes include ESD, brownout, config, battery | Replay policy is FTA judgment |
| Venue networking | FIRST | https://ftc-resources.firstinspires.org/ftc/event/venue-networking | accessed 2026-08-17 | Yes | Each robot hosts 5 GHz AP; channel planning | Event ops, not OpMode API |
| Control Hub ports / ESD | FIRST ftc-docs | https://ftc-docs.firstinspires.org/en/latest/control_hard_compon/rc_components/hub/ports/ch-ports.html | accessed 2026-08-17 | Yes | USB 2.0 ESD can disrupt Wi-Fi; use USB 3.0 for cameras | Hardware placement |
| Managing ESD | FIRST ftc-docs | https://ftc-docs.firstinspires.org/en/latest/hardware_and_software_configuration/configuring/managing_esd/managing-esd.html | linked from REV | Yes | ESD mitigation | Complementary to REV |
| REV general troubleshooting | REV | https://docs.revrobotics.com/duo-control/troubleshooting-the-control-system/troubleshooting-the-control-system.md | accessed 2026-08-17 | Yes | USB 3.0 camera; grounding strap; isolate issues | Not a latency spec |
| REV Control Hub troubleshooting | REV | https://docs.revrobotics.com/duo-control/troubleshooting-the-control-system/control-hub-troubleshooting | accessed 2026-08-17 | Yes | CH-specific paths | Follow current tree |
| REV Expansion Hub troubleshooting | REV | https://docs.revrobotics.com/duo-control/troubleshooting-the-control-system/expansion-hub-troubleshooting | accessed 2026-08-17 | Yes | EH cable/power/firmware | Follow current tree |
| REV Driver Hub troubleshooting | REV | https://docs.revrobotics.com/duo-control/troubleshooting-the-control-system/driver-hub-troubleshooting.md | accessed 2026-08-17 | Yes | Wi-Fi-after-sleep; keep DS app open; brownout symptoms when mechanisms run | OS issues change with updates |
| REV LED blink codes | REV | https://docs.revrobotics.com/duo-control/troubleshooting-the-control-system/led-blink-codes.md | accessed 2026-08-17 | Yes | Blinking blue = keepalive timeout; orange = <7 V | Timeout ms not given |
| REV Log Viewer | REV | https://docs.revrobotics.com/rev-hardware-client/duo/log-viewer | accessed 2026-08-17 | Yes | Post-match logs | Not runtime API |
| REV-31-1595 user manual | REV | https://revrobotics.ca/content/docs/REV-31-1595-UM.pdf | accessed 2026-08-17 | Yes | Failsafe at communication loss; LED keepalive | PDF may lag docs site |
| Heartbeat class | FIRST | https://javadoc.io/static/org.firstinspires.ftc/RobotCore/6.2.1/com/qualcomm/robotcore/robocol/Heartbeat.html | 6.2.1 | Internal | Alive-check message | Older javadoc; method still on 11.2.0 manager |
| LynxModule | FIRST / OpenRC | https://first-tech-challenge.github.io/SkyStone/com/qualcomm/hardware/lynx/LynxModule.html | historical | Mostly | isNotResponding; health warning helper | SkyStone-era pages; verify on current SDK |
| I2cDeviceSynchImpl | FIRST | https://first-tech-challenge.github.io/SkyStone/com/qualcomm/robotcore/hardware/I2cDeviceSynchImpl.html | historical | Mostly | Heartbeat interval default 0 | Verify on current SDK |
| Watchdog timer issue | community | https://github.com/FIRST-Tech-Challenge/FtcRobotController/issues/555 | GitHub issue | Partial | Mentions ~10 s RC app keepalive (`FtcAccessPointService`) | **Different** from DS heartbeat; app-level; not OpMode API |
| Community disconnect thread | FIRST community | https://ftc-community.firstinspires.org/t/control-hubs-disconnecting-randomly/1230 | accessed 2026-08-17 | Anecdote | FTA: motors may run until comms-loss stop; ESD vs brownout distinction | Not a spec |
| WPILib DS log viewer | WPILib | https://docs.wpilib.org/en/stable/docs/software/driverstation/driver-station-log-viewer.html | stable | FRC only | Voltage, trip time, lost packets, reboot signatures | Patterns transfer; files do not |
| WPILib DriverStation | WPILib | https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/wpilibj/DriverStation.html | 2026.2.2 | FRC only | `isDSAttached()` | **Not on Control Hub** |
| WPILib Watchdog | WPILib | https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/wpilibj/Watchdog.html | 2026.2.2 | FRC only | Loop timeout callback | Different meaning from FTC DS watchdog |
| AdvantageKit | FRC 6328 | https://docs.advantagekit.org/getting-started/what-is-advantagekit/ | accessed 2026-08-17 | FRC | Log all inputs; replay | Do not import |
| AdvantageKit swerve template | FRC 6328 | https://docs.advantagekit.org/getting-started/template-projects/spark-swerve-template/ | accessed 2026-08-17 | FRC | Disconnected-device dashboard alerts | FRC CAN |
| PurpleLib | LASA Robotics | https://github.com/lasarobotics/PurpleLib/ | accessed 2026-08-17 | FRC | Hardware wrappers, health, recovery | WPILib vendors |
| gm0 LynxModule | game manual 0 | https://gm0.org/en/latest/docs/software/adv-control-system/lynx-module.html | accessed 2026-08-17 | Secondary | How teams obtain LynxModule | Community guide, not FIRST |
| AMPER | The Allsparks | https://github.com/The-Allsparks/AMPER | 2026 | Yes | Electrical sibling conventions | — |
| MIMIC | The Allsparks | https://github.com/The-Allsparks/MIMIC | 2026 | Yes | Mechanism sibling | — |
| ViDAR | The Allsparks | https://github.com/The-Allsparks/ViDAR | 2026 | Yes | Perception sibling | — |

Obsolete links were replaced with the official 2026–2027 manual and current REV markdown docs. SkyStone Javadoc URLs are marked historical and must be re-checked against RobotCore 11.x before competition adapters are written.
