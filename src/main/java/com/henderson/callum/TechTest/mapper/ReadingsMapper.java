package com.henderson.callum.TechTest.mapper;

import com.henderson.callum.TechTest.api.request.ReadingsRequest;
import com.henderson.callum.TechTest.model.Readings;
import com.henderson.callum.TechTest.api.response.ReadingsResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReadingsMapper {

    ReadingsResponse modelToResponse(Readings readings);
    Readings requestToModel(ReadingsRequest readingsRequest);
}
