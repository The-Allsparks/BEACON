# Failure domains

Treat these independently. BEACON must not call every unavailable device a “network failure.”

## Gamepad to Driver Hub

Possible causes: loose or damaged USB; gamepad power/reset; Driver Hub USB damage; re-enumeration or assignment loss; two identical controllers.

**OpMode-visible:** last gamepad state, maybe `timestamp`, maybe id unassociated.

**Not visible:** whether USB physically dropped vs DS still sending zeros.

## Driver Station to Robot Controller

Possible causes: RF congestion; channel selection; attenuation; Driver Hub sleep or Wi-Fi-after-sleep defect; RC Wi-Fi subsystem disruption; ESD; software/version mismatch; field-control event.

**OpMode-visible:** official OpMode stop after the SDK timeout.

**Not visible:** heartbeat age, peer events, ping, malicious vs accidental interference.

## Robot Controller to Control Hub I/O

Possible causes: internal service failure; keepalive timeout; firmware; power instability; ESD; blocked SDK operation.

**Visible on hardware:** blinking blue LED (keepalive timeout).

**Not visible to OpMode:** the keepalive timer itself.

## Control Hub to Expansion Hub

Possible causes: communication cable; connector strain; module address/configuration; Expansion Hub power; firmware; ESD.

**OpMode-visible (later adapter):** `LynxModule.isNotResponding()`, bulk-read failures.

## USB camera path

Possible causes: loose cable; USB enumeration; hub or port; bandwidth; ESD; camera failure; ViDAR pipeline stall.

**Special:** USB 2.0 camera placement can disrupt **Wi-Fi**, which is a different domain that *looks* like DS loss. Prefer USB 3.0.

## Sensor buses

Possible causes: I²C lockup; disconnected sensor; wiring; address conflict; invalid/stale data; bus-level timeout.

**OpMode-visible:** I²C synch health, implausible readings reported by MIMIC.

## Software loop

Possible causes: blocking call; deadlock; exception; CPU saturation; vision load; excessive telemetry; synchronous device recovery.

**OpMode-visible:** loop duration measured in user code.

## Electrical system

Possible causes: brownout; loose XT30 or switch; damaged wiring; voltage sag; intermittent power.

**Owner:** AMPER. BEACON correlates AMPER events with comms symptoms; it does not diagnose batteries.

## Classification rule

If evidence is missing, the domain is `UNKNOWN` and the reason is `INSUFFICIENT_EVIDENCE` or `NEVER_OBSERVED`. Do not guess “jamming.”
