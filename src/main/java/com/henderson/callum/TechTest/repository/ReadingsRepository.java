package com.henderson.callum.TechTest.repository;

import com.henderson.callum.TechTest.model.GasReadings;
import com.henderson.callum.TechTest.model.Readings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingsRepository extends JpaRepository<Readings, Long> {

    public Optional<Readings> findByAccountId(Long id);
}
