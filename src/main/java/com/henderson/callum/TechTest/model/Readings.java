package com.henderson.callum.TechTest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
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
    @Builder.Default
    private List<GasReadings> gasReadings = new ArrayList<>();

    @ElementCollection
    @Builder.Default
    private List<ElecReadings> elecReadings = new ArrayList<>();
}
