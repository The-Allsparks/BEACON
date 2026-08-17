package org.allsparks.beacon.health;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.allsparks.beacon.api.FailureDomain;
import org.allsparks.beacon.api.HealthReport;
import org.allsparks.beacon.api.LinkHealth;
import org.allsparks.beacon.api.LinkId;
import org.allsparks.beacon.clock.BeaconClock;

/**
 * Collects health reports without owning the underlying devices.
 * Manual reports from ViDAR, MIMIC, AMPER, Pedro, or robot code are stored
 * before automatic adapters exist.
 */
public final class HealthRegistry {
    private final BeaconClock clock;
    private final Map<LinkId, HealthSource> sources = new LinkedHashMap<>();
    private final Map<LinkId, HealthReport> lastReports = new LinkedHashMap<>();

    public HealthRegistry(BeaconClock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public void register(HealthSource source) {
        Objects.requireNonNull(source, "source");
        sources.put(source.id(), source);
    }

    /**
     * Store a manual report. Creates a {@link FakeHealthSource} when the id is new
     * so sibling libraries can report before adapters exist.
     */
    public LinkHealth report(HealthReport report) {
        Objects.requireNonNull(report, "report");
        HealthSource source = sources.get(report.id());
        if (source == null) {
            FakeHealthSource created = new FakeHealthSource(report.id(), report.domain());
            sources.put(report.id(), created);
            source = created;
        }
        source.accept(report);
        lastReports.put(report.id(), report);
        return source.sample(clock.nanoTime());
    }

    public LinkHealth report(String id, FailureDomain domain, HealthReport report) {
        Objects.requireNonNull(report, "report");
        if (!LinkId.of(id).equals(report.id())) {
            throw new IllegalArgumentException("id mismatch");
        }
        if (report.domain() != domain) {
            throw new IllegalArgumentException("domain mismatch");
        }
        return report(report);
    }

    public Optional<LinkHealth> get(LinkId id) {
        HealthSource source = sources.get(id);
        if (source == null) {
            return Optional.empty();
        }
        return Optional.of(source.sample(clock.nanoTime()));
    }

    public Optional<HealthReport> lastReport(LinkId id) {
        return Optional.ofNullable(lastReports.get(id));
    }

    public List<LinkHealth> snapshot() {
        long now = clock.nanoTime();
        List<LinkHealth> result = new ArrayList<>(sources.size());
        for (HealthSource source : sources.values()) {
            result.add(source.sample(now));
        }
        return Collections.unmodifiableList(result);
    }

    public int size() {
        return sources.size();
    }
}
