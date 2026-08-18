package org.allsparks.beacon.correlate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.allsparks.beacon.api.Confidence;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.LinkFailureReason;
import org.allsparks.beacon.api.LinkHealth;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.api.LinkState;
import org.allsparks.beacon.log.BeaconEvent;
import org.allsparks.beacon.log.BeaconEventType;

/**
 * Phase 4 advisory correlator. Classifies a snapshot plus event timeline into
 * one label with confidence and evidence. Does not probe devices, capture
 * Wi-Fi, claim jamming, or command actuators.
 *
 * <p>Simultaneous failures in more than one domain family default to
 * {@link AdvisoryLabel#INSUFFICIENT_EVIDENCE} unless electrical or AMPER
 * voltage evidence is present, in which case the power label is used at low
 * confidence rather than inventing a single non-power cause.
 */
public final class EventCorrelator {
    static final Confidence ISOLATED = Confidence.of(0.5);
    static final Confidence POWER_WITH_COMPANIONS = Confidence.of(0.4);

    private static final LinkId CORRELATOR_ID = LinkId.of("correlator");

    private EventCorrelator() {}

    public static AdvisoryReport evaluate(List<BeaconEvent> events, List<LinkHealth> snapshot) {
        Objects.requireNonNull(events, "events");
        Objects.requireNonNull(snapshot, "snapshot");
        List<LinkHealth> failed = new ArrayList<>();
        for (LinkHealth health : snapshot) {
            Objects.requireNonNull(health, "health");
            if (isFailed(health)) {
                failed.add(health);
            }
        }
        boolean amperVoltage = false;
        for (BeaconEvent event : events) {
            Objects.requireNonNull(event, "event");
            if (event.type() == BeaconEventType.AMPER_VOLTAGE) {
                amperVoltage = true;
            }
        }
        List<AdvisoryEvidence> evidence = evidenceFrom(failed, events, amperVoltage);
        Set<DomainFamily> families = familiesOf(failed);
        if (amperVoltage) {
            families.add(DomainFamily.POWER);
        }
        if (families.isEmpty()) {
            return AdvisoryReport.of(
                    AdvisoryLabel.INSUFFICIENT_EVIDENCE,
                    Confidence.unknown(),
                    evidence,
                    "No STALE or LOST links and no AMPER voltage events. Nothing to classify.");
        }
        if (families.contains(DomainFamily.UNSUPPORTED)) {
            return insufficient(
                    evidence,
                    "Snapshot includes a domain this correlator will not diagnose "
                            + "(Driver Station, gamepad, or unknown). Prefer insufficient evidence.");
        }
        if (families.size() == 1) {
            DomainFamily only = families.iterator().next();
            if (only == DomainFamily.CAMERA) {
                return AdvisoryReport.of(
                        AdvisoryLabel.PROBABLE_ISOLATED_CAMERA_FAILURE,
                        ISOLATED,
                        evidence,
                        "Only USB camera links are STALE or LOST. Other observed domains are not failed.");
            }
            if (only == DomainFamily.HUB_PATH) {
                return AdvisoryReport.of(
                        AdvisoryLabel.PROBABLE_EXPANSION_HUB_PATH_FAILURE,
                        ISOLATED,
                        evidence,
                        "Only Expansion Hub path or sensor-bus links are STALE or LOST.");
            }
            if (only == DomainFamily.POWER) {
                return AdvisoryReport.of(
                        AdvisoryLabel.PROBABLE_POWER_DISRUPTION,
                        ISOLATED,
                        evidence,
                        "Electrical or AMPER voltage evidence is present and no other domain family is failed.");
            }
            if (only == DomainFamily.LOOP) {
                if (hasLoopOverrun(failed)) {
                    return AdvisoryReport.of(
                            AdvisoryLabel.PROBABLE_LOOP_OVERRUN,
                            ISOLATED,
                            evidence,
                            "Only software-loop links are failed, and a LOOP_OVERRUN reason is present.");
                }
                return insufficient(
                        evidence,
                        "A software-loop link is STALE or LOST without a LOOP_OVERRUN reason. "
                                + "Localization loss is not treated as a loop overrun.");
            }
        }
        if (families.contains(DomainFamily.POWER) && families.size() > 1) {
            return AdvisoryReport.of(
                    AdvisoryLabel.PROBABLE_POWER_DISRUPTION,
                    POWER_WITH_COMPANIONS,
                    evidence,
                    "Electrical or AMPER voltage evidence coincides with other failed domains. "
                            + "Simultaneous failures can also be ESD or cabling; this is not a unique cause "
                            + "and is not jamming.");
        }
        return insufficient(
                evidence,
                "More than one domain family is STALE or LOST without electrical or AMPER evidence. "
                        + "Simultaneous failures are not treated as a single root cause.");
    }

    private static AdvisoryReport insufficient(List<AdvisoryEvidence> evidence, String explanation) {
        return AdvisoryReport.of(
                AdvisoryLabel.INSUFFICIENT_EVIDENCE, Confidence.unknown(), evidence, explanation);
    }

    private static boolean isFailed(LinkHealth health) {
        return health.state() == LinkState.STALE || health.state() == LinkState.LOST;
    }

    private static boolean hasLoopOverrun(List<LinkHealth> failed) {
        for (LinkHealth health : failed) {
            if (health.domain() == FailureDomain.SOFTWARE_LOOP
                    && health.reason() == LinkFailureReason.LOOP_OVERRUN) {
                return true;
            }
        }
        return false;
    }

    private static Set<DomainFamily> familiesOf(List<LinkHealth> failed) {
        Set<DomainFamily> families = new LinkedHashSet<>();
        for (LinkHealth health : failed) {
            families.add(familyOf(health.domain()));
        }
        return families;
    }

    private static DomainFamily familyOf(FailureDomain domain) {
        switch (domain) {
            case USB_CAMERA:
                return DomainFamily.CAMERA;
            case CONTROL_HUB_TO_EXPANSION_HUB:
            case SENSOR_BUS:
                return DomainFamily.HUB_PATH;
            case ELECTRICAL:
                return DomainFamily.POWER;
            case SOFTWARE_LOOP:
                return DomainFamily.LOOP;
            case GAMEPAD_TO_DRIVER_HUB:
            case DRIVER_STATION_TO_ROBOT_CONTROLLER:
            case ROBOT_CONTROLLER_TO_CONTROL_HUB_IO:
            case UNKNOWN:
            default:
                return DomainFamily.UNSUPPORTED;
        }
    }

    private static List<AdvisoryEvidence> evidenceFrom(
            List<LinkHealth> failed, List<BeaconEvent> events, boolean amperVoltage) {
        List<AdvisoryEvidence> evidence = new ArrayList<>();
        for (LinkHealth health : failed) {
            evidence.add(new AdvisoryEvidence(
                    health.id(),
                    health.domain(),
                    health.state().name() + " " + health.reason().name()));
        }
        for (BeaconEvent event : events) {
            if (event.type() == BeaconEventType.AMPER_VOLTAGE) {
                LinkId id = event.linkId() == null ? LinkId.of("amper") : event.linkId();
                evidence.add(new AdvisoryEvidence(
                        id, event.domain(), event.type().name() + " " + event.detail()));
            }
        }
        if (evidence.isEmpty()) {
            evidence.add(new AdvisoryEvidence(
                    CORRELATOR_ID,
                    FailureDomain.UNKNOWN,
                    "No failed links and no AMPER voltage events in the provided window."));
        } else if (amperVoltage && failed.size() > 1) {
            evidence.add(new AdvisoryEvidence(
                    CORRELATOR_ID,
                    FailureDomain.ELECTRICAL,
                    "Multiple domain families failed in the same window; do not treat that as jamming."));
        }
        return Collections.unmodifiableList(evidence);
    }

    private enum DomainFamily {
        CAMERA,
        HUB_PATH,
        POWER,
        LOOP,
        UNSUPPORTED
    }
}
