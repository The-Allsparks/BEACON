# Troubleshooting and physical reliability

Software cannot replace construction. Work this list **before** blaming BEACON or “the network.”

## Physical checklist

- Secure Control Hub placement; antenna-aware structure (do not bury the antenna in metal)
- USB strain relief; short, good-quality camera cables
- Secure Expansion Hub RS-485 / communication cable; strain relief on connectors
- Protected Driver Hub USB ports; do not wrap cables around the Hub
- Current firmware; matched Robot Controller and Driver Station app versions
- 5 GHz when the DS device supports it; event-directed channel if staff assign one
- ESD mitigation: resistive grounding strap; anti-static field spray where used
- **Cameras on Control Hub USB 3.0**, not USB 2.0 (REV/FIRST: USB 2.0 ESD can upset the Wi-Fi chip)
- Healthy battery; inspect XT30 and switch connections
- Prevent Driver Hub sleep: screen on, DS app open
- Charged, legal Driver Hub power support

## Symptom mapping (advisory)

| What you see | Do not conclude | Check first |
|--------------|-----------------|-------------|
| DS disconnect sound + voltage cliff | Jamming | AMPER / battery / XT30 |
| Disconnect when a mechanism runs | Wi-Fi bug | Brownout (REV: ~9 V class symptoms) |
| Camera dies and Wi-Fi dies together | Two unrelated bugs | USB 2.0 ESD path |
| Expansion Hub LED blinking blue | DS Wi-Fi | Keepalive / cable / power |
| Gamepad dead, robot still enabled | Full DS loss | USB gamepad / assignment |
| Robot drives after you think you disconnected | BEACON failed | Official timeout has not elapsed |

## Logs

Post-match: RC `robotControllerLog.txt`, REV Hardware Client log viewer, BEACON CSV, AMPER CSV. Do not capture other teams’ Wi-Fi.
