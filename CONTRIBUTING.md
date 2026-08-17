# Contributing to BEACON

BEACON is maintained by [The Allsparks](https://github.com/The-Allsparks) (FTC Team 36117) for our team and the wider FTC community.

## Setup

```powershell
git clone https://github.com/The-Allsparks/BEACON.git
cd BEACON
.\gradlew.bat test
```

## Rules of engagement

1. **Do not enable motor, servo, or network intervention** in PRs without explicit maintainer review and documented acceptance tests.
2. Phases 0–4 must remain behavior-neutral for actuators and the official FTC network.
3. Distinguish **verified fact**, **observed implementation**, **engineering inference**, and **untested hypothesis** in documentation.
4. Never describe an FRC Driver Station or WPILib API as a current FTC OpMode capability without evidence.
5. Do not commit secrets, Wi-Fi passwords, tokens, or student PII.
6. Do not use private or internal SDK APIs in competition code.

## Pull requests

- Prefer small, reviewable PRs.
- Include motivation, phase impact, test evidence, and safety notes.
- Update docs when behavior or maturity labels change.
- Run `.\gradlew.bat test` (or `./gradlew test`) before requesting review.

## Line endings

The repository stores LF line endings (see [.gitattributes](.gitattributes)).

## License

Contributions are accepted under the MIT License ([LICENSE](LICENSE)). No CLA is required.
