package com.henderson.callum.TechTest.application;

import com.henderson.callum.TechTest.exceptions.DuplicateReadingException;
import com.henderson.callum.TechTest.model.ElecReadings;
import com.henderson.callum.TechTest.model.GasReadings;
import com.henderson.callum.TechTest.model.Readings;
import com.henderson.callum.TechTest.repository.ReadingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ReadingsApplication {

    @Autowired
    private ReadingsRepository readingsRepository;

    @Autowired
    private EnrichmentHelper enrichmentHelper;

    @Transactional
    public Optional<Readings> getReadingsByAccountNumber(Long accountId) {
        return readingsRepository.findByAccountId(accountId);
    }

    @Transactional
    public Readings addReadings(Readings readings) throws DuplicateReadingException {
        verifyDuplicate(readings);
        Optional<Readings> oldReadingsOptional = readingsRepository.findByAccountId(readings.getAccountId());
        if (oldReadingsOptional.isPresent()) {
            Readings oldReadings = oldReadingsOptional.get();
            oldReadings.getGasReadings().addAll(readings.getGasReadings());
            oldReadings.getElecReadings().addAll(readings.getElecReadings());
            readings = oldReadings;

            readings.setGasReadings(enrichmentHelper.enrichGasReadings(readings.getGasReadings()));
            readings.setElecReadings(enrichmentHelper.enrichElecReadings(readings.getElecReadings()));
        }
        return readingsRepository.save(readings);
    }


    private void verifyDuplicate(Readings readings) throws DuplicateReadingException {
        Optional<Readings> readingsOptional = getReadingsByAccountNumber(readings.getAccountId());
        if (readingsOptional.isPresent()) {
            Readings oldReadings = readingsOptional.get();

            List<GasReadings> duplicateGasReadings = new ArrayList<>();
            List<ElecReadings> duplicateElecReadings = new ArrayList<>();
            if (!readings.getGasReadings().isEmpty() && !oldReadings.getGasReadings().isEmpty()) {
                duplicateGasReadings = oldReadings.getGasReadings().stream().filter(oldReading -> readings.getGasReadings().stream()
                                .anyMatch(newReading -> newReading.getId().equals(oldReading.getId()) && newReading.getDate().isEqual(oldReading.getDate())))
                        .collect(Collectors.toList());
            }

            if (!readings.getElecReadings().isEmpty() && !oldReadings.getElecReadings().isEmpty()) {
                duplicateElecReadings = oldReadings.getElecReadings().stream().filter(oldReading -> readings.getElecReadings().stream()
                                .anyMatch(newReading -> newReading.getId().equals(oldReading.getId()) && newReading.getDate().isEqual(oldReading.getDate())))
                        .collect(Collectors.toList());
            }

            if (duplicateElecReadings.isEmpty() && duplicateGasReadings.isEmpty()) {
                return;
            } else {
                throw new DuplicateReadingException("Duplicate readings have been uploaded.\nGas [" + duplicateGasReadings + "]\nElectric [" + duplicateElecReadings + "]");
            }

        } else {
            return;
        }
    }

    @PostConstruct
    @Transactional
    public void addData() {

        GasReadings gasReadings = GasReadings.builder().id(Long.valueOf(4231)).reading(Long.valueOf(42314231)).date(LocalDate.now()).meterId(Long.valueOf(42311324)).build();
        ElecReadings elecReadings = ElecReadings.builder().id(Long.valueOf(2413)).reading(Long.valueOf(24132413)).date(LocalDate.now()).meterId(Long.valueOf(24133142)).build();
        Readings readings = Readings.builder().accountId(Long.valueOf(54321)).elecReadings(Arrays.asList(elecReadings)).gasReadings(Arrays.asList(gasReadings)).build();
        readingsRepository.save(readings);

        GasReadings gasReadingsSolus = GasReadings.builder().id(Long.valueOf(4231)).reading(Long.valueOf(42314231)).date(LocalDate.now()).meterId(Long.valueOf(42311324)).build();
        Readings readingsSolus = Readings.builder().accountId(Long.valueOf(1234)).gasReadings(Arrays.asList(gasReadingsSolus)).build();
        readingsRepository.save(readingsSolus);
    }
}
