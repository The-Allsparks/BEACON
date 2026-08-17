package org.allsparks.beacon.lease;

import java.util.Objects;
import org.allsparks.beacon.api.CommandSource;

/**
 * Time-limited teleop command. Java 11 compatible equivalent of the conceptual
 * record API. Do not renew a Driver Station lease merely because the OpMode
 * loop continues executing.
 *
 * @param <T> command payload type
 */
public final class CommandLease<T> {
    private final T command;
    private final long sourceTimestampNanos;
    private final long expirationTimestampNanos;
    private final CommandSource source;

    public CommandLease(
            T command,
            long sourceTimestampNanos,
            long expirationTimestampNanos,
            CommandSource source) {
        this.command = command;
        this.sourceTimestampNanos = sourceTimestampNanos;
        this.expirationTimestampNanos = expirationTimestampNanos;
        this.source = Objects.requireNonNull(source, "source");
        if (expirationTimestampNanos < sourceTimestampNanos) {
            throw new IllegalArgumentException("Lease expiration must be >= source timestamp");
        }
    }

    public boolean isFresh(long nowNanos) {
        return nowNanos <= expirationTimestampNanos;
    }

    public T command() {
        return command;
    }

    public long sourceTimestampNanos() {
        return sourceTimestampNanos;
    }

    public long expirationTimestampNanos() {
        return expirationTimestampNanos;
    }

    public CommandSource source() {
        return source;
    }
}
