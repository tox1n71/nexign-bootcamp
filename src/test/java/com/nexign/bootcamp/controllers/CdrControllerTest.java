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
public class CdrControllerTest {

    @MockBean
    private ReportService reportService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGenerateCdrReport_Success() throws Exception {
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        when(reportService.generateCdrReport(eq("7999000001"), any(), any())).thenReturn(uuid);

        mockMvc.perform(get("/api/cdr/report")
                        .param("msisdn", "7999000001")
                        .param("start", "2025-01-01T00:00:00Z")
                        .param("end", "2025-12-31T23:59:59Z"))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(jsonPath("$.status").value("success")) // Статус "success"
                .andExpect(jsonPath("$.uuid").value(uuid)); // Возвращённый UUID
    }
}
