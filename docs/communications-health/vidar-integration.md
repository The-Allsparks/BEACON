# ViDAR integration

ViDAR owns field perception and camera/pipeline health.

## Manual report (Phase 1)

```java
beacon.report(HealthReport.healthy(
        LinkId.of("frontCamera"),
        FailureDomain.USB_CAMERA,
        frameTimestampNanos,
        "ViDAR"));
```

Use the **frame timestamp**, not the OpMode loop time, as `sourceTimestampNanos`.

## Domain traps

A camera USB fault on the Control Hub USB 2.0 port can disrupt Wi-Fi. That is two domains at once. Report the camera as `USB_CAMERA` and let correlation notice DS symptoms separately.

## Phase 7

Bounded recovery of a single camera may be delegated to ViDAR under a `RecoveryPolicy`. Remaining cameras must keep running. Do not block the robot loop on USB reset.
