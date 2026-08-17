# Safe state

Safe state is **subsystem-specific**. BEACON requests; it does not command every motor.

Until Phase 5+ flags are enabled after review, all requests are documentation and shadow types only.

## Drivetrain (teleop, only if DS freshness is verified)

- command zero movement;
- use appropriate zero-power/brake behavior;
- cancel driver-assist movement;
- invalidate the prior drive intent;
- prevent automatic restoration of stale commands.

## Intake and conveyors

- stop recoverable rollers and conveyors;
- preserve a passive game-piece grip where safe;
- require fresh intent before restarting.

## Elevator, arm, and extensions

Delegate to MIMIC:

- stop profile progression;
- retain safe holding behavior;
- use counterbalance or ratchet appropriately;
- do not suddenly remove necessary holding power;
- do not automatically return home;
- do not engage a ratchet under unsafe speed or load.

## Servos

Per mechanism: retain last safe position, or move to a proven-safe position. Avoid repeated actuation during unstable communication.

## ViDAR

Continue passive observation if CPU and power remain healthy. Do not initiate new autonomous action because the Driver Station disappeared. Preserve logs.

## Pedro and autonomous

Distinguish official autonomous from teleop driver-command freshness. Respect official enable/disable/stop. Do not drive to hide a lost teleop connection.

## Official stop always wins

If the FTC SDK stops the OpMode, that stop is authoritative. BEACON must not keep actuators alive.
