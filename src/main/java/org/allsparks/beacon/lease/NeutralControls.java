package org.allsparks.beacon.lease;

/**
 * Neutral-control detector used by recovery inhibit.
 * Thresholds are magnitudes on a typical [-1, 1] gamepad axis.
 */
public final class NeutralControls {
    private final double stickDeadband;
    private final double triggerDeadband;

    public NeutralControls(double stickDeadband, double triggerDeadband) {
        if (stickDeadband < 0.0 || triggerDeadband < 0.0) {
            throw new IllegalArgumentException("Deadbands must be >= 0");
        }
        this.stickDeadband = stickDeadband;
        this.triggerDeadband = triggerDeadband;
    }

    public static NeutralControls typical() {
        return new NeutralControls(0.05, 0.05);
    }

    public boolean isNeutral(
            double leftStickX,
            double leftStickY,
            double rightStickX,
            double rightStickY,
            double leftTrigger,
            double rightTrigger) {
        return magnitude(leftStickX) <= stickDeadband
                && magnitude(leftStickY) <= stickDeadband
                && magnitude(rightStickX) <= stickDeadband
                && magnitude(rightStickY) <= stickDeadband
                && magnitude(leftTrigger) <= triggerDeadband
                && magnitude(rightTrigger) <= triggerDeadband;
    }

    private static double magnitude(double value) {
        return Math.abs(value);
    }
}
