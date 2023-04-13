package com.henderson.callum.TechTest.application;

import com.henderson.callum.TechTest.model.ElecReadings;
import com.henderson.callum.TechTest.model.GasReadings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@SpringBootTest
public class EnrichmentHelperTests {

    @InjectMocks
    private EnrichmentHelper enrichmentHelper;
    private List<GasReadings> gasReadings;
    private List<ElecReadings> elecReadings;

    @BeforeEach
    public void setup() {
        gasReadings = new ArrayList<>(Arrays.asList(
                GasReadings.builder().id(Long.valueOf(4231)).reading(42314231l).date(LocalDate.now()).meterId(42311324l).build(),
                GasReadings.builder().id(Long.valueOf(4231)).reading(42264231l).date(LocalDate.now().minusDays(20)).meterId(42311324l).build()
        ));
        elecReadings = new ArrayList<>(Arrays.asList(
                ElecReadings.builder().id(Long.valueOf(4231)).reading(42314231l).date(LocalDate.now()).meterId(42311324l).build(),
                ElecReadings.builder().id(Long.valueOf(4231)).reading(42264231l).date(LocalDate.now().minusDays(20)).meterId(42311324l).build()
        ));
    }

    @Test
    public void enrichGasReadings() {
        List<GasReadings> enrichedGasReadings = enrichmentHelper.enrichGasReadings(gasReadings);
        assertThat(enrichedGasReadings.get(0),
                is(GasReadings.builder().id(Long.valueOf(4231)).reading(42314231l).date(LocalDate.now()).meterId(42311324l).avgDailyUsage(2500l).periodSinceLastRead(20l).usageSinceLastRead(50000l).build()));
        assertThat(enrichedGasReadings.get(1),
                is(GasReadings.builder().id(Long.valueOf(4231)).reading(42264231l).date(LocalDate.now().minusDays(20)).meterId(42311324l).avgDailyUsage(0l).periodSinceLastRead(0l).usageSinceLastRead(0l).build()));
    }

    @Test
    public void enrichElecReadings() {
        List<ElecReadings> enrichedGasReadings = enrichmentHelper.enrichElecReadings(elecReadings);
        assertThat(enrichedGasReadings.get(0),
                is(ElecReadings.builder().id(Long.valueOf(4231)).reading(42314231l).date(LocalDate.now()).meterId(42311324l).avgDailyUsage(2500l).periodSinceLastRead(20l).usageSinceLastRead(50000l).build()));
        assertThat(enrichedGasReadings.get(1),
                is(ElecReadings.builder().id(Long.valueOf(4231)).reading(42264231l).date(LocalDate.now().minusDays(20)).meterId(42311324l).avgDailyUsage(0l).periodSinceLastRead(0l).usageSinceLastRead(0l).build()));
    }
}
