package com.nexign.bootcamp.services;

import com.nexign.bootcamp.entities.Cdr;
import com.nexign.bootcamp.repos.CdrRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ReportServiceTest {

    @Mock
    private CdrRepository cdrRepository;

    @InjectMocks
    private ReportService reportService;

    private OffsetDateTime start;
    private OffsetDateTime end;
    private String msisdn;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        start = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        end = OffsetDateTime.parse("2025-12-31T23:59:59Z");
        msisdn = "7999000001";
    }

    @Test
    public void testGenerateCdrReport_Success() throws IOException {
        Cdr cdr = new Cdr();
        cdr.setCallType("01");
        cdr.setCallerMsisdn(msisdn);
        cdr.setReceiverMsisdn("7999000002");
        cdr.setStartTime(start);
        cdr.setEndTime(end);

        when(cdrRepository.findByCallerMsisdnAndStartTimeBetween(eq(msisdn), any(), any()))
                .thenReturn(List.of(cdr));

        String uuid = reportService.generateCdrReport(msisdn, start, end);
        assertNotNull(uuid);
        File file = new File("./" + msisdn + "_" + uuid + ".csv");
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testGenerateCdrReport_NoRecords() {
        when(cdrRepository.findByCallerMsisdnAndStartTimeBetween(eq(msisdn), any(), any()))
                .thenReturn(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reportService.generateCdrReport(msisdn, start, end);
        });
        assertEquals("No CDR records found for the given MSISDN and time range.", exception.getMessage());
    }
}