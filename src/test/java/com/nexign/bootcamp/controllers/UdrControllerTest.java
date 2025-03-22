package com.nexign.bootcamp.controllers;

import com.nexign.bootcamp.repos.CdrRepository;
import com.nexign.bootcamp.services.ReportService;
import com.nexign.bootcamp.services.UdrService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UdrControllerTest {


    @MockBean
    private UdrService udrService;


    @Autowired
    private MockMvc mockMvc;
    @Test
    public void testGetUdrByMsisdn_Success() throws Exception {
        Map<String, Object> mockUdr = new HashMap<>();
        mockUdr.put("msisdn", "7999000001");
        mockUdr.put("incomingCall", Map.of("totalTime", "00:01:00"));
        mockUdr.put("outcomingCall", Map.of("totalTime", "00:02:00"));
        when(udrService.getUdrByMsisdn(anyString(), any(), any())).thenReturn(mockUdr);

        mockMvc.perform(get("/api/udr/subscriber")
                        .param("msisdn", "7999000001")
                        .param("start", "2025-01-01T00:00:00Z")
                        .param("end", "2025-12-31T23:59:59Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value("7999000001"))
                .andExpect(jsonPath("$.incomingCall.totalTime").value("00:01:00"))
                .andExpect(jsonPath("$.outcomingCall.totalTime").value("00:02:00"));
    }

    @Test
    public void testGetUdrByMsisdn_NoDates() throws Exception {
        Map<String, Object> mockUdr = new HashMap<>();
        mockUdr.put("msisdn", "7999000001");
        mockUdr.put("incomingCall", Map.of("totalTime", "01:00:00"));
        mockUdr.put("outcomingCall", Map.of("totalTime", "04:00:00"));
        when(udrService.getUdrByMsisdn(anyString(), any(), any())).thenReturn(mockUdr);

        mockMvc.perform(get("/api/udr/subscriber")
                        .param("msisdn", "7999000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value("7999000001"))
                .andExpect(jsonPath("$.incomingCall.totalTime").value("01:00:00"))
                .andExpect(jsonPath("$.outcomingCall.totalTime").value("04:00:00"));
    }

    @Test
    public void testGetUderForAllSubscribers_Success() throws Exception {
        List<Map<String, Object>> mockUdrList = List.of(
                Map.of("msisdn", "7999000001", "incomingCall", Map.of("totalTime", "00:01:00"), "outcomingCall", Map.of("totalTime", "00:02:00")),
                Map.of("msisdn", "7999000002", "incomingCall", Map.of("totalTime", "00:03:00"), "outcomingCall", Map.of("totalTime", "00:04:00"))
        );
        when(udrService.getUdrForAllSubscribers(any(), any())).thenReturn(mockUdrList);

        mockMvc.perform(get("/api/udr/all")
                        .param("month", "2025-04"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].msisdn").value("7999000001"))
                .andExpect(jsonPath("$[0].incomingCall.totalTime").value("00:01:00"))
                .andExpect(jsonPath("$[0].outcomingCall.totalTime").value("00:02:00"))
                .andExpect(jsonPath("$[1].msisdn").value("7999000002"))
                .andExpect(jsonPath("$[1].incomingCall.totalTime").value("00:03:00"))
                .andExpect(jsonPath("$[1].outcomingCall.totalTime").value("00:04:00"));
    }

}
