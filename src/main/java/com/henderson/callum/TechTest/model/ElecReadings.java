package com.henderson.callum.TechTest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ElecReadings {

    private Long id;

    private Long meterId;

    private Long reading;

    private Long usageSinceLastRead;

    private Long periodSinceLastRead;

    private Long avgDailyUsage;

    private LocalDate date;

}
