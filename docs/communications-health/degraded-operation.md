# Degraded operation

Phase 8. Disabled by default.

Consumers may respond to health **after** the meaning of each degraded mode is documented and deterministic.

| Consumer | Honest degraded behavior |
|----------|--------------------------|
| ViDAR | Reduce coverage and confidence; do not invent detections |
| Pedro | Decline high-precision actions |
| MIMIC | Disable sensor-dependent movement; keep holding where required |
| Autonomous | Only preapproved safe fallbacks |
| AMPER | Remain available to identify power involvement |
| Drive (teleop) | Remain available where safe and where official enable allows |

Loss of confidence must not silently produce confident autonomous action. Unaffected manual driving remains available only where it is safe and legal.
