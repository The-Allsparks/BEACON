# Rules analysis

Access date: 2026-08-17. Primary source: official **FIRST Tech Challenge 2026–2027 BIOBUZZ™ Competition Manual, Version V0 (updated 31 Jul 2026)** at [ftc-resources.firstinspires.org/ftc/game/manual](https://ftc-resources.firstinspires.org/ftc/game/manual).

This is not legal advice. Teams must re-read the current manual and Q&A before every event.

## Verified constraints that bind BEACON

| Rule | Exact supported claim | BEACON implication |
|------|----------------------|--------------------|
| **E301** | Teams may not set up their own Wi-Fi, Bluetooth, or other 2.4 GHz / 5 GHz communications systems in the venue. | No second robot-control network. No experimental access point. |
| **E302** | Participants may not interfere, attempt to interfere, or attempt to connect with any other team or FIRST wireless network without expressed permission. | No packet capture of other robots, no deauthentication detection, no channel hopping as a probe. |
| **R704 A** | Teams may not use any other form of wireless communication, except those offered by the provided official tools, to communicate to, from, or within the ROBOT. | BEACON must not transmit unauthorized match communication. |
| **R704 B** | All communication signals must originate from only the ROBOT CONTROLLER or DRIVER STATION using the ROBOT CONTROLLER Wi-Fi network. No other devices may attempt to connect to, interfere with, or alter that network. | No inject, modify, or spoof of official control packets. |
| **R704 C** | Programming laptops and other devices (other than the DRIVER STATION) must be disconnected from the ROBOT CONTROLLER Wi-Fi network during MATCH play. | Post-match log tooling is off-robot and off-match. |
| **R704 D** | Software with access to the ROBOT CONTROLLER Wi-Fi network must limit streamed data. Software may only stream robot control data, debugging data, and telemetry using the FTC Driver Station Application. Additional logging/streaming services such as FTC Dashboard are prohibited. No continuous video stream is allowed. | Runtime BEACON telemetry must stay inside official DS telemetry. Do not add a second stream. |
| **R704 E** | If event staff assign a band or channel, teams must use it. | Channel selection is an event-directed physical control, not a BEACON feature. |
| **R706** | DRIVER STATION device and software, ROBOT CONTROLLER device, power switch(es), and related core devices shall not be tampered with except listed exceptions (firmware updates with manufacturer firmware are allowed). | Do not patch official apps or Hub OS as a BEACON feature. |
| **R711 C** | On RC and DS Android devices, Wi-Fi must be enabled and Bluetooth must be disabled. | Confirms Bluetooth is not a legal match-control path. |
| **R904** | Other than the RC app and DS app connection, no other wireless communications shall be used to communicate to, from, or within the OPERATOR CONSOLE during a MATCH. | No extra DS-side radios. |

## Classification

- **Verified fact:** the quoted rules exist in the official 2026–2027 manual HTML as of the access date.
- **Engineering inference:** a library that only records in-memory health and writes official telemetry is compatible with R704 D; a custom Wi-Fi dashboard is not.
- **Untested hypothesis:** none of the rules were interpreted by FIRST staff for this project. If a later phase needs private SDK hooks or extra network traffic, stop and review against the then-current manual.

## BEACON must not

- transmit unauthorized communication during a match;
- create a second robot-control network;
- modify or inject official control packets;
- probe or attack wireless networks;
- perform channel hopping during match play;
- attempt deauthentication detection through prohibited packet capture;
- interfere with field infrastructure;
- claim to determine whether interference is malicious;
- weaken official watchdog or stop behavior.

Any experimental network tooling must be reviewed against the current official FTC rules before use.
