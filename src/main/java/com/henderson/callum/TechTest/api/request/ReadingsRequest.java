package com.henderson.callum.TechTest.api.request;

import com.henderson.callum.TechTest.model.ElecReadings;
import com.henderson.callum.TechTest.model.GasReadings;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingsRequest {

    @NotNull
    private Long accountId;

    @Builder.Default
    private List<GasReadings> gasReadings = new ArrayList<>();

    @Builder.Default
    private List<ElecReadings> elecReadings = new ArrayList<>();
}
