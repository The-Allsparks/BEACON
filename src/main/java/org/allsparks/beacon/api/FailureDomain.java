package org.allsparks.beacon.api;

/**
 * Independent communication or supporting domains. Do not collapse every
 * unavailable device into a single "network failure."
 */
public enum FailureDomain {
    UNKNOWN,
    GAMEPAD_TO_DRIVER_HUB,
    DRIVER_STATION_TO_ROBOT_CONTROLLER,
    ROBOT_CONTROLLER_TO_CONTROL_HUB_IO,
    CONTROL_HUB_TO_EXPANSION_HUB,
    USB_CAMERA,
    SENSOR_BUS,
    SOFTWARE_LOOP,
    ELECTRICAL
}
