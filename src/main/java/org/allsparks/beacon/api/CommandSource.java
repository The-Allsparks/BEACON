package org.allsparks.beacon.api;

/** Origin of a time-limited command lease. */
public enum CommandSource {
    DRIVER_STATION_GAMEPAD,
    AUTONOMOUS,
    DRIVER_ASSIST,
    SUBSYSTEM,
    UNKNOWN
}
