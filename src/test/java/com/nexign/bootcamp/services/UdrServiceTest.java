package com.nexign.bootcamp.services;

import com.nexign.bootcamp.entities.Cdr;
import com.nexign.bootcamp.entities.Subscriber;
import com.nexign.bootcamp.repos.CdrRepository;
import com.nexign.bootcamp.repos.SubscriberRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UdrServiceTest {

    @Mock
    private CdrRepository cdrRepository;

    @Mock
    private SubscriberRepository subscriberRepository;

    @InjectMocks
    private UdrService udrService;

    @Test
    public void testGetUdrByMsisdn() {
        Cdr cdr = new Cdr();
        cdr.setCallType("01");
        cdr.setCallerMsisdn("7999000001");
        cdr.setReceiverMsisdn("7999000002");
        cdr.setStartTime(OffsetDateTime.parse("2025-01-01T10:00:00Z"));
        cdr.setEndTime(OffsetDateTime.parse("2025-01-01T10:01:00Z"));

        when(cdrRepository.findByCallerMsisdnAndStartTimeBetween(
                "7999000001",
                OffsetDateTime.parse("2025-01-01T00:00:00Z"),
                OffsetDateTime.parse("2025-12-31T23:59:59Z")))
                .thenReturn(List.of(cdr));

        Map<String, Object> udr = udrService.getUdrByMsisdn(
                "7999000001",
                OffsetDateTime.parse("2025-01-01T00:00:00Z"),
                OffsetDateTime.parse("2025-12-31T23:59:59Z"));
        System.out.println(udr);
        assertEquals("7999000001", udr.get("msisdn"));
        Map<String, String> outgoing = (Map<String, String>) udr.get("outgoingCall");
        assertEquals("00:01:00", outgoing.get("totalTime"));
    }

    @Test
    public void testGetUdrForAllSubscribers() {
        Subscriber sub1 = new Subscriber("7999000001");
        Subscriber sub2 = new Subscriber("7999000002");
        when(subscriberRepository.findAll()).thenReturn(List.of(sub1, sub2));

        Cdr cdr1 = new Cdr();
        cdr1.setCallType("01");
        cdr1.setCallerMsisdn("7999000001");
        cdr1.setReceiverMsisdn("7999000002");
        cdr1.setStartTime(OffsetDateTime.parse("2025-01-01T10:00:00Z"));
        cdr1.setEndTime(OffsetDateTime.parse("2025-01-01T10:01:00Z"));

        Cdr cdr2 = new Cdr();
        cdr2.setCallType("02");
        cdr2.setCallerMsisdn("7999000002");
        cdr2.setReceiverMsisdn("7999000001");
        cdr2.setStartTime(OffsetDateTime.parse("2025-01-01T11:00:00Z"));
        cdr2.setEndTime(OffsetDateTime.parse("2025-01-01T11:02:00Z"));

        Cdr cdr3 = new Cdr();
        cdr3.setCallType("01");
        cdr3.setCallerMsisdn("7999000002");
        cdr3.setReceiverMsisdn("7999000001");
        cdr3.setStartTime(OffsetDateTime.parse("2025-01-01T12:00:00Z"));
        cdr3.setEndTime(OffsetDateTime.parse("2025-01-01T12:04:00Z"));

        Cdr cdr4 = new Cdr();
        cdr4.setCallType("02");
        cdr4.setCallerMsisdn("7999000001");
        cdr4.setReceiverMsisdn("7999000002");
        cdr4.setStartTime(OffsetDateTime.parse("2025-01-01T13:00:00Z"));
        cdr4.setEndTime(OffsetDateTime.parse("2025-01-01T13:03:00Z"));

        when(cdrRepository.findByCallerMsisdnAndStartTimeBetween(eq("7999000001"), any(), any()))
                .thenReturn(List.of(cdr1));
        when(cdrRepository.findByReceiverMsisdnAndStartTimeBetween(eq("7999000001"), any(), any()))
                .thenReturn(List.of(cdr2));
        when(cdrRepository.findByCallerMsisdnAndStartTimeBetween(eq("7999000002"), any(), any()))
                .thenReturn(List.of(cdr3));
        when(cdrRepository.findByReceiverMsisdnAndStartTimeBetween(eq("7999000002"), any(), any()))
                .thenReturn(List.of(cdr4));

        List<Map<String, Object>> udrList = udrService.getUdrForAllSubscribers(
                OffsetDateTime.parse("2025-01-01T00:00:00Z"),
                OffsetDateTime.parse("2025-12-31T23:59:59Z"));

        assertEquals(2, udrList.size());
        assertEquals("7999000001", udrList.get(0).get("msisdn"));
        assertEquals("7999000002", udrList.get(1).get("msisdn"));

        Map<String, String> outgoing1 = (Map<String, String>) udrList.get(0).get("outgoingCall");
        assertEquals("00:01:00", outgoing1.get("totalTime"));
        Map<String, String> incoming1 = (Map<String, String>) udrList.get(0).get("incomingCall");
        assertEquals("00:02:00", incoming1.get("totalTime"));

        Map<String, String> outgoing2 = (Map<String, String>) udrList.get(1).get("outgoingCall");
        assertEquals("00:04:00", outgoing2.get("totalTime"));
        Map<String, String> incoming2 = (Map<String, String>) udrList.get(1).get("incomingCall");
        assertEquals("00:03:00", incoming2.get("totalTime"));
    }
}