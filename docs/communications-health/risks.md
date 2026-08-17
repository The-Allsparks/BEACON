# Risks and unresolved questions

1. **DS packet cadence while sticks are held** — untested on Allsparks Driver Hub hardware.
2. **Official stop latency** — wiki says >2 s; not measured with a restrained robot yet.
3. **Hub keepalive timeout milliseconds** — not published as an OpMode constant.
4. **Whether `LynxModule.isNotResponding()` is sticky or momentary** on current SDK — verify before Phase 2 Hub preflight.
5. **Gamepad.timestamp updates on analog hold** — device dependent; do not ship detectors on it.
6. **SystemCore communications surface** — undocumented here.
7. **False correlation** of ESD + brownout + USB camera reset in one instant.
8. **R704 D telemetry budget** — even official telemetry can congest; BEACON logs must stay bounded.
9. **Private API temptation** — `EventLoopManager.getHeartbeat()` looks convenient and is still wrong for this library.
10. **Manual 2026–2027 V0** — rules may change in Team Updates; re-read before events.

Do not implement active safe-stop or recovery until maintainers review this list.
