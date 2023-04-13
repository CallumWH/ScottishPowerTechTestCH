package com.henderson.callum.TechTest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.henderson.callum.TechTest.api.response.ReadingsResponse;
import com.henderson.callum.TechTest.application.ReadingsApplication;
import com.henderson.callum.TechTest.exceptions.DuplicateReadingException;
import com.henderson.callum.TechTest.mapper.ReadingsMapper;
import com.henderson.callum.TechTest.mapper.ReadingsMapperImpl;
import com.henderson.callum.TechTest.model.ElecReadings;
import com.henderson.callum.TechTest.model.GasReadings;
import com.henderson.callum.TechTest.model.Readings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ContextConfiguration(classes = {TechTestService.class, ReadingsMapperImpl.class})
@WebMvcTest
public class TechTestServiceTests {

    @MockBean
    private ReadingsApplication app;
    @Autowired
    private MockMvc mockMvc;
    private Readings readings;
    private ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    @BeforeEach
    public void setup() {
        objectMapper.setDateFormat(new SimpleDateFormat("dd-M-yyyy hh:mm:ss"));
        GasReadings gasReadings = GasReadings.builder().id(Long.valueOf(4231)).reading(Long.valueOf(42314231)).date(LocalDate.now().minusDays(1)).meterId(Long.valueOf(42311324)).build();
        ElecReadings elecReadings = ElecReadings.builder().id(Long.valueOf(2413)).reading(Long.valueOf(24132413)).date(LocalDate.now().minusDays(1)).meterId(Long.valueOf(24133142)).build();
        readings = Readings.builder().accountId(Long.valueOf(54321)).elecReadings(new ArrayList<>(Arrays.asList(elecReadings))).gasReadings(new ArrayList<>(Arrays.asList(gasReadings))).build();
    }

    @Test
    public void getByAccountId() throws Exception {

        when(app.getReadingsByAccountNumber(54321l)).thenReturn(Optional.of(readings));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/smart/reads/54321")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString(), is(objectMapper.writeValueAsString(readings)));
    }

    @Test
    public void getByAccountIdDoesNotExist() throws Exception {

        when(app.getReadingsByAccountNumber(54321l)).thenReturn(Optional.empty());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/smart/reads/54321")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void createReadings() throws Exception {

        when(app.addReadings(any())).thenReturn(readings);

        String jsonString = objectMapper.writeValueAsString(readings);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/smart/reads")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        verify(app).addReadings(readings);
    }

    @Test
    public void createReadingsDuplicate() throws Exception {

        when(app.addReadings(any())).thenThrow(new DuplicateReadingException("message"));

        String jsonString = objectMapper.writeValueAsString(readings);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/smart/reads")
                        .content(jsonString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

    }
}
