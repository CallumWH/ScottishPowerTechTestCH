package com.henderson.callum.TechTest.service;

import com.henderson.callum.TechTest.api.request.ReadingsRequest;
import com.henderson.callum.TechTest.application.ReadingsApplication;
import com.henderson.callum.TechTest.exceptions.DuplicateReadingException;
import com.henderson.callum.TechTest.mapper.ReadingsMapper;
import com.henderson.callum.TechTest.model.Readings;
import com.henderson.callum.TechTest.api.response.ReadingsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class TechTestService {

    @Autowired
    private ReadingsApplication readingsApplication;

    @Autowired
    private ReadingsMapper mapper;
    @GetMapping(value = "api/smart/reads/{ACCOUNTNUMBER}")
    public ResponseEntity<ReadingsResponse> getMeterReadingForAccount(@PathVariable("ACCOUNTNUMBER") Long accountNumber) {
        Optional<Readings> readingsOptional = readingsApplication.getReadingsByAccountNumber(accountNumber);
        if(readingsOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "unable to locate readings with account id [" + accountNumber + "]");
        } else {
            return new ResponseEntity<ReadingsResponse>(mapper.modelToResponse(readingsOptional.get()), HttpStatus.OK);
        }
    }

    @PostMapping(value = "api/smart/reads", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ReadingsResponse> createReadings(@RequestBody ReadingsRequest readingsRequest) {
        Readings response = null;
        try {
            response = readingsApplication.addReadings(mapper.requestToModel(readingsRequest));
        } catch (DuplicateReadingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
        return new ResponseEntity<>(mapper.modelToResponse(response), HttpStatus.CREATED);
    }
}
