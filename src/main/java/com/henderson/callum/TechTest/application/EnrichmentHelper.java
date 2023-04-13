package com.henderson.callum.TechTest.application;

import com.henderson.callum.TechTest.model.ElecReadings;
import com.henderson.callum.TechTest.model.GasReadings;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class EnrichmentHelper {
    public List<GasReadings> enrichGasReadings(List<GasReadings> gasReadings) {
        gasReadings = EnrichLastRead(gasReadings);
        gasReadings = EnrichGasUsageSinceLastRead(gasReadings);
        gasReadings = EnrichGasAverageDailyUsage(gasReadings);
        return gasReadings;
    }

    public List<GasReadings> EnrichLastRead(List<GasReadings> gasReadings) {
        gasReadings.stream().filter(g -> g.getPeriodSinceLastRead() == null).forEach(g -> {
            Optional<GasReadings> mostRecent = getMostRecentGasReading(gasReadings, g);
            if (mostRecent.isPresent()) {
                g.setPeriodSinceLastRead(ChronoUnit.DAYS.between(mostRecent.get().getDate(), g.getDate()));
            } else {
                g.setPeriodSinceLastRead(0l);
            }
        });
        return gasReadings;
    }

    public List<GasReadings> EnrichGasUsageSinceLastRead(List<GasReadings> gasReadings) {
        gasReadings.stream().filter(g -> g.getUsageSinceLastRead() == null).forEach(g -> {
            Optional<GasReadings> mostRecent = getMostRecentGasReading(gasReadings, g);
            if (mostRecent.isPresent()) {
                g.setUsageSinceLastRead(g.getReading() - mostRecent.get().getReading());
            } else {
                g.setUsageSinceLastRead(0l);
            }
        });
        return gasReadings;
    }

    public List<GasReadings> EnrichGasAverageDailyUsage(List<GasReadings> gasReadings) {
        gasReadings.stream().filter(g -> g.getAvgDailyUsage() == null).forEach(g -> {
            if (g.getPeriodSinceLastRead() != 0l) {
                g.setAvgDailyUsage(g.getUsageSinceLastRead() / g.getPeriodSinceLastRead());
            } else {
                g.setAvgDailyUsage(0l);
            }
        });
        return gasReadings;
    }

    public Optional<GasReadings> getMostRecentGasReading(List<GasReadings> gasReadings, GasReadings toEnrich) {
        return gasReadings.stream().filter(g -> g.getDate().isBefore(toEnrich.getDate())).max(Comparator.comparing(g -> g.getDate()));
    }

    public List<ElecReadings> enrichElecReadings(List<ElecReadings> elecReadings) {
        elecReadings = EnrichElecLastRead(elecReadings);
        elecReadings = EnrichElecUsageSinceLastRead(elecReadings);
        elecReadings = EnrichElecAverageDailyUsage(elecReadings);
        return elecReadings;
    }

    public List<ElecReadings> EnrichElecLastRead(List<ElecReadings> elecReadings) {
        elecReadings.stream().filter(e -> e.getPeriodSinceLastRead() == null).forEach(e -> {
            Optional<ElecReadings> mostRecent = getMostRecentElecReading(elecReadings, e);
            if (mostRecent.isPresent()) {
                e.setPeriodSinceLastRead(ChronoUnit.DAYS.between(mostRecent.get().getDate(), e.getDate()));
            } else {
                e.setPeriodSinceLastRead(0l);
            }
        });
        return elecReadings;
    }

    public List<ElecReadings> EnrichElecUsageSinceLastRead(List<ElecReadings> elecReadings) {
        elecReadings.stream().filter(e -> e.getUsageSinceLastRead() == null).forEach(e -> {
            Optional<ElecReadings> mostRecent = getMostRecentElecReading(elecReadings, e);
            if (mostRecent.isPresent()) {
                e.setUsageSinceLastRead(e.getReading() - mostRecent.get().getReading());
            } else {
                e.setUsageSinceLastRead(0l);
            }
        });
        return elecReadings;
    }

    public List<ElecReadings> EnrichElecAverageDailyUsage(List<ElecReadings> elecReadings) {
        elecReadings.stream().filter(e -> e.getAvgDailyUsage() == null).forEach(e -> {
            if (e.getPeriodSinceLastRead() != 0l) {
                e.setAvgDailyUsage(e.getUsageSinceLastRead() / e.getPeriodSinceLastRead());
            } else {
                e.setAvgDailyUsage(0l);
            }
        });
        return elecReadings;
    }

    public Optional<ElecReadings> getMostRecentElecReading(List<ElecReadings> elecReadings, ElecReadings toEnrich) {
        return elecReadings.stream().filter(e -> e.getDate().isBefore(toEnrich.getDate())).max(Comparator.comparing(g -> g.getDate()));
    }
}
