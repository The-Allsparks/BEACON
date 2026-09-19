package org.allsparks.beacon.log;

/**
 * Optional observer of BEACON events. TRACE, tests, or DS glue implement this.
 * BEACON does not import TRACE. Default is {@link #NOOP}.
 *
 * <p>Called on the OpMode thread from {@code report()} / {@code observe()} /
 * {@code preflight()} / {@code advise()}. Must not block, write files, or
 * command actuators or radios.
 */
public interface BeaconEventSink {
    void onEvent(BeaconEvent event);

    BeaconEventSink NOOP = new BeaconEventSink() {
        @Override
        public void onEvent(BeaconEvent event) {
            // intentionally empty
        }
    };
}
