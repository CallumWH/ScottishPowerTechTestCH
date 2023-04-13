package com.henderson.callum.TechTest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "readings")
public class Readings {

    @Id
    @Column(name = "ID")
    private Long accountId;

    @ElementCollection
    private List<GasReadings> gasReadings;

    @ElementCollection
    private List<ElecReadings> elecReadings;
}
