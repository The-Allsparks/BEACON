# Glossary

| Term | Meaning in BEACON |
|------|-------------------|
| **Connection** | A vaguely used word. Always name the **link** instead. |
| **Link** | One named path: gamepad USB, DS Wi-Fi, Hub I/O, Expansion cable, camera USB, I²C, or a software stream. |
| **Bus** | Shared electrical/data path (I²C, RS-485, USB) that can fail independently of Wi-Fi. |
| **Packet** | One datagram on a protocol. OpModes do not see Robocol packets. |
| **Heartbeat** | Periodic “I am alive” message. FTC DS/RC heartbeats are **not** exposed to OpModes. |
| **Keepalive** | Hub firmware timer; blinking blue LED means it expired. |
| **Timeout** | Age beyond which data is treated as lost. Per-source. |
| **Latency** | Delay of a measured round trip. Leave empty if not measured. |
| **Jitter** | Variation in packet or loop timing. Ordinary jitter must not trip safe-stop. |
| **Packet loss** | Missing datagrams. Visible on FRC DS logs; not a public FTC OpMode API. |
| **Freshness** | Whether the **source** has been validated recently, not whether the loop ran. |
| **Stale data** | Last observation is too old to trust but not yet classified lost. |
| **Watchdog** | Official FTC/RC stop-on-disconnect and Hub fail-safe. BEACON must not weaken it. Also: FRC loop-overrun watchdog, which is a different API. |
| **Command lease** | Time-limited permission to apply a teleop command. |
| **Safe state** | Subsystem-specific rest. Not always “zero power.” |
| **Recovery inhibit** | After reconnect, refuse to reapply the old command until fresh + neutral (+ optional re-arm). |
| **Degraded operation** | Honest reduced capability with documented limits. |
| **ESD** | Electrostatic discharge. Can reset USB or Wi-Fi without being a “network attack.” |
| **Brownout** | Voltage collapse that can reboot radios or Hubs and mimic comms loss. |
| **USB enumeration** | Host rediscovering a USB device after reset. |
| **Confidence** | Evidence strength in `[0, 1]`, or explicitly unknown. |
| **False positive** | Declaring a fault or safe-stop when the robot was actually healthy. |
| **Fault correlation** | Time-aligning events across domains without claiming a single root cause. |
