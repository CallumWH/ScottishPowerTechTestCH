package com.henderson.callum.TechTest.api.response;

import com.henderson.callum.TechTest.model.ElecReadings;
import com.henderson.callum.TechTest.model.GasReadings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingsResponse {

    private Long accountId;
    private List<GasReadings> gasReadings;
    private List<ElecReadings> elecReadings;
}
