package com.nexign.bootcamp.controllers;

import com.nexign.bootcamp.services.UdrService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для обработки REST-запросов, связанных с UDR (Usage Detail Records).
 * Предоставляет эндпоинты для получения UDR для конкретного абонента или всех абонентов за указанный период.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/udr")
public class UdrController {

    private final UdrService udrService;

    /**
     * Получает UDR для конкретного абонента за указанный период.
     * Если параметры start и end не указаны, используется текущая дата и годовой диапазон по умолчанию.
     *
     * @param msisdn номер телефона абонента (MSISDN)
     * @param start начальная дата и время в формате ISO-8601 (опционально)
     * @param end конечная дата и время в формате ISO-8601 (опционально)
     * @return карта с данными UDR для абонента
     */
    @GetMapping("/subscriber")
    public Map<String, Object> getUdrByMsisdn(
            @RequestParam("msisdn") String msisdn,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        OffsetDateTime startTime = start != null ? OffsetDateTime.parse(start) : OffsetDateTime.now();
        OffsetDateTime endTime = end != null ? OffsetDateTime.parse(end) : startTime.plusYears(1).minusSeconds(1);
        return udrService.getUdrByMsisdn(msisdn, startTime, endTime);
    }

    /**
     * Получает UDR для всех абонентов за указанный месяц.
     * Принимает параметр месяца в формате "YYYY-MM" и возвращает UDR для всех абонентов за этот период.
     *
     * @param month строка с месяцем в формате "YYYY-MM" (например, "2025-04")
     * @return список карт с данными UDR для всех абонентов
     */
    @GetMapping("/all")
    public List<Map<String, Object>> getUdrForAllSubscribers(@RequestParam String month) {
        OffsetDateTime startTime = OffsetDateTime.parse(month + "-01T00:00:00Z");
        OffsetDateTime endTime = startTime.plusMonths(1).minusSeconds(1);
        return udrService.getUdrForAllSubscribers(startTime, endTime);
    }
}