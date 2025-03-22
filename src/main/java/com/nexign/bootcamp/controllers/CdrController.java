package com.nexign.bootcamp.controllers;

import com.nexign.bootcamp.services.ReportService;
import com.nexign.bootcamp.services.UdrService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для обработки REST запросов UDR.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/cdr")

/**
 * Контроллер для обработки REST-запросов, связанных с CDR (Call Detail Records).
 * Предоставляет эндпоинты для генерации отчетов по CDR для указанного абонента и временного диапазона.
 */
public class CdrController {
    private final ReportService reportService;

    /**
     * Генерирует отчет CDR для указанного абонента за заданный период.
     * Принимает номер телефона (MSISDN), начальную и конечную даты, генерирует отчет и возвращает статус и UUID файла.
     *
     * @param msisdn номер телефона абонента (MSISDN)
     * @param start начальная дата и время в формате ISO-8601 (например, "2025-01-01T00:00:00Z")
     * @param end конечная дата и время в формате ISO-8601 (например, "2025-12-31T23:59:59Z")
     * @return карта с ключами "status" и "uuid" при успехе или "status" и "message" при ошибке
     * @throws IOException если произошла ошибка при генерации файла
     */
    @GetMapping("/report")
    public Map<String, String> generateCdrReport(
            @RequestParam String msisdn,
            @RequestParam String start,
            @RequestParam String end) throws IOException {
        try {
            OffsetDateTime startTime = OffsetDateTime.parse(start);
            OffsetDateTime endTime = OffsetDateTime.parse(end);
            String uuid = reportService.generateCdrReport(msisdn, startTime, endTime);
            return Map.of("status", "success", "uuid", uuid);
        } catch (IllegalArgumentException e) {
            return Map.of("status", "failed", "message", e.getMessage());
        }
    }
}
