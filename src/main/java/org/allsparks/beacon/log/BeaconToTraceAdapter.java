package org.allsparks.beacon.log;

import java.util.Objects;
import org.allsparks.beacon.api.LinkId;

/**
 * Maps {@link BeaconEvent} onto TRACE channel names. BEACON does not import
 * TRACE. TeamCode supplies the {@link Emitter} that calls TRACE when BEACON is
 * composed.
 */
public final class BeaconToTraceAdapter implements BeaconEventSink {
    public interface Emitter {
        void event(String name, String message);
    }

    private final Emitter emitter;

    public BeaconToTraceAdapter(Emitter emitter) {
        this.emitter = Objects.requireNonNull(emitter, "emitter");
    }

    @Override
    public void onEvent(BeaconEvent event) {
        if (event == null) {
            return;
        }
        emitter.event(signalName(event), payload(event));
    }

    public static String signalName(BeaconEvent event) {
        LinkId id = event.linkId();
        String link = sanitize(id == null ? "" : id.value());
        String type = event.type() == null ? "EVENT" : event.type().name();
        if (link.isEmpty()) {
            return "BEACON/" + type;
        }
        return "BEACON/" + link + "/" + type;
    }

    static String payload(BeaconEvent event) {
        return event.timestampNanos()
                + " "
                + event.domain().name()
                + " "
                + event.detail();
    }

    static String sanitize(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_') {
                sb.append(ch);
            } else {
                sb.append('_');
            }
        }
        if (sb.length() == 0) {
            return "";
        }
        if (sb.charAt(0) >= '0' && sb.charAt(0) <= '9') {
            sb.insert(0, 'B');
        }
        return sb.toString();
    }
}
