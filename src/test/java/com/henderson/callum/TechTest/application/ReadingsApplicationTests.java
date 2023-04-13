package com.henderson.callum.TechTest.application;

import com.henderson.callum.TechTest.exceptions.DuplicateReadingException;
import com.henderson.callum.TechTest.model.ElecReadings;
import com.henderson.callum.TechTest.model.GasReadings;
import com.henderson.callum.TechTest.model.Readings;
import com.henderson.callum.TechTest.repository.ReadingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@SpringBootTest
public class ReadingsApplicationTests {

    @Mock
    private EnrichmentHelper enrichmentHelper;
    @Mock
    private ReadingsRepository repo;

    @InjectMocks
    private ReadingsApplication app;
    private Readings readings;

    @BeforeEach
    public void setup() {
        GasReadings gasReadings = GasReadings.builder().id(Long.valueOf(4231)).reading(Long.valueOf(42314231)).date(LocalDate.now().minusDays(1)).meterId(Long.valueOf(42311324)).build();
        ElecReadings elecReadings = ElecReadings.builder().id(Long.valueOf(2413)).reading(Long.valueOf(24132413)).date(LocalDate.now().minusDays(1)).meterId(Long.valueOf(24133142)).build();
        readings = Readings.builder().accountId(Long.valueOf(54321)).elecReadings(new ArrayList<>(Arrays.asList(elecReadings))).gasReadings(new ArrayList<>(Arrays.asList(gasReadings))).build();
    }

    @Test
    @Transactional
    public void getReadings() {
        Long accountId = 54321l;
        when(repo.findByAccountId(accountId)).thenReturn(Optional.of(readings));
        Optional<Readings> resultOptional = app.getReadingsByAccountNumber(accountId);
        assertThat(readings, is(resultOptional.get()));
    }

    @Test
    @Transactional
    public void getReadingsInvalid() {
        Long accountId = 1l;
        when(repo.findByAccountId(accountId)).thenReturn(Optional.empty());
        Optional<Readings> readingsOptional = app.getReadingsByAccountNumber(accountId);
        assertThat(Optional.empty(), is(readingsOptional));
    }

    @Test
    public void addReadings() throws DuplicateReadingException {
        Long accountId = 54321l;
        GasReadings newGasReadings = GasReadings.builder().id(4231l).reading(42314631l).date(LocalDate.now()).meterId(42311324l).build();
        List<GasReadings> compositeGasReadings = new ArrayList<>(Arrays.asList(readings.getGasReadings().get(0), newGasReadings));
        Readings newReadings = Readings.builder().accountId(Long.valueOf(accountId)).gasReadings(Arrays.asList(newGasReadings)).build();
        Readings compositeReadings = Readings.builder().elecReadings(readings.getElecReadings()).gasReadings(readings.getGasReadings()).accountId(accountId).build();
        compositeReadings.setGasReadings(compositeGasReadings);

        when(enrichmentHelper.enrichGasReadings(compositeGasReadings)).thenReturn(compositeGasReadings);
        when(enrichmentHelper.enrichElecReadings(compositeReadings.getElecReadings())).thenReturn(compositeReadings.getElecReadings());
        when(repo.findByAccountId(accountId)).thenReturn(Optional.of(readings));

        app.addReadings(newReadings);
        verify(repo).save(compositeReadings);
    }

    @Test
    public void addReadingsFirstEntry() throws DuplicateReadingException {
        Long accountId = 54321l;

        when(enrichmentHelper.enrichGasReadings(readings.getGasReadings())).thenReturn(readings.getGasReadings());
        when(enrichmentHelper.enrichElecReadings(readings.getElecReadings())).thenReturn(readings.getElecReadings());
        when(repo.findByAccountId(accountId)).thenReturn(Optional.empty());

        app.addReadings(readings);
        verify(repo).save(readings);
    }

    @Test
    public void addReadingsDuplicate() throws DuplicateReadingException {
        Long accountId = 54321l;
        when(repo.findByAccountId(accountId)).thenReturn(Optional.of(readings));
        Exception exception = assertThrows(DuplicateReadingException.class, () -> {app.addReadings(readings);});
        assertThat(exception.getClass(), is(DuplicateReadingException.class));
        verify(repo, never()).save(any());
    }
}
