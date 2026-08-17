# Organization convention assessment (The Allsparks)

Prepared before BEACON creation. `The-Allsparks/BEACON` did **not** previously exist.

## Repositories inspected

| Repo | Visibility | Default branch | License | Notes |
|------|------------|----------------|---------|-------|
| [AMPER](https://github.com/The-Allsparks/AMPER) | Public | `main` | MIT | Closest Java-library analog (Gradle `java-library`, Phase 0 docs, CI) |
| [MIMIC](https://github.com/The-Allsparks/MIMIC) | Public | `main` | MIT | Sibling mechanism-safety library; MIT; public |
| [ViDAR](https://github.com/The-Allsparks/ViDAR) | Public | `main` | MIT | Perception library; mixed Python/Java |
| [ftc-dev-tools](https://github.com/The-Allsparks/ftc-dev-tools) | Public | `main` | Apache-2.0 | Tooling monorepo; richest governance templates |
| [ftc-team-analysis](https://github.com/The-Allsparks/ftc-team-analysis) | Public | `main` | MIT | Web tool |
| SponsorshipPlan | Private | `main` | — | Ignored for OSS library norms |

## Conventions adopted for BEACON

| Topic | Followed from | BEACON choice |
|-------|---------------|---------------|
| Public OSS | AMPER / MIMIC / ViDAR | Public |
| License | AMPER / MIMIC / ViDAR | **MIT** (compatible with JUnit 5 EPL+GPL classpath; no FTC SDK shipped in this library) |
| Branch | Org default | `main` |
| Java + Gradle | AMPER | Root `java-library`, Java 11, CI Temurin 17 |
| Package naming | AMPER `org.allsparks.amper` | `org.allsparks.beacon` |
| LF + `.gitattributes` | AMPER / ViDAR | Yes |
| CoC / SECURITY / PR template / Dependabot | AMPER | Adapted (communications safety language) |
| Issue templates | AMPER | Bug / feature / phase work |
| CI | AMPER | `./gradlew check` on Ubuntu and Windows plus docs-structure job |
| Topics | Requested list | Applied on GitHub |

## Deliberate differences

- **No FTC SDK compile dependency** in Phase 0: BEACON must compile on a desktop JVM. SDK types appear only in documentation and future adapters.
- **Conceptual records implemented as final classes:** FTC TeamCode commonly targets Java 8/11; Java 16 records would break that path.
- **Driver Station safe-stop not implemented:** unlike a typical feature scaffold, the feasibility study concluded that a supported public freshness API is not available to ordinary OpMode code.
