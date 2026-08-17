# Security Policy

## Supported versions

| Version | Supported |
| ------- | --------- |
| 0.1.x   | Yes       |

## Reporting a vulnerability

Please do **not** open a public issue for security problems that could put robots, students, or machines at risk.

Prefer:

1. GitHub Security Advisories for this repository (when available), or
2. A private email contact published by the maintainers

Include:

- A description of the issue
- Steps to reproduce
- Impact assessment (for example: unexpected motor motion, bypass of official stop behavior, credential exposure, unauthorized wireless probing)

## Safety expectations for this project

BEACON intentionally:

- Keeps **actuator and network intervention disabled by default**
- Treats missing or stale observations as a reason to **avoid** active response, not invent values
- Documents that software cannot fix wiring, connectors, ESD, brownouts, or radio congestion
- Must not weaken official FTC watchdog or stop behavior

If you discover a path that enables output intervention without an explicit feature flag, injects or modifies official control packets, or probes wireless networks, treat it as a safety defect.

## Secrets

Never store passwords, Wi-Fi credentials, API keys, or tokens in the repository, issues, or exported logs.
