package com.nexign.bootcamp.services;

import com.nexign.bootcamp.entities.Cdr;
import com.nexign.bootcamp.repos.CdrRepository;
import com.nexign.bootcamp.repos.SubscriberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Сервис для генерации UDR отчетов.
 *  Вычисляет общее время входящих и исходящих звонков для абонентов на основе CDR-записей.
 */
@Service
@AllArgsConstructor
public class UdrService {
    private final CdrRepository cdrRepository;
    private final SubscriberRepository subscriberRepository;

    /**
     * Получает UDR для конкретного абонента за указанный период.
     * Вычисляет общее время входящих и исходящих звонков для заданного MSISDN.
     *
     * @param msisdn номер телефона абонента (MSISDN)
     * @param start начальная дата и время
     * @param end конечная дата и время
     * @return карта с данными UDR, включая MSISDN и общее время звонков
     */
    public Map<String, Object> getUdrByMsisdn(String msisdn, OffsetDateTime start, OffsetDateTime end) {
        List<Cdr> cdrs = cdrRepository.findByCallerMsisdnAndStartTimeBetween(msisdn, start, end);
        List<Cdr> receivedCdrs = cdrRepository.findByReceiverMsisdnAndStartTimeBetween(msisdn, start, end);
        long incomingSeconds = receivedCdrs.stream()
                .filter( cdr -> "02".equals(cdr.getCallType()) && msisdn.equals(cdr.getReceiverMsisdn()))
                .mapToLong(cdr -> ChronoUnit.SECONDS.between(cdr.getStartTime(), cdr.getEndTime()))
                .sum();
        long outgoingSeconds = cdrs.stream()
                .filter(cdr -> "01".equals(cdr.getCallType()))
                .mapToLong(cdr -> ChronoUnit.SECONDS.between(cdr.getStartTime(), cdr.getEndTime()))
                .sum();
        return buildUdr(msisdn, incomingSeconds, outgoingSeconds);
    }
    /**
     * Получает UDR для всех абонентов за указанный период.
     * Генерирует список UDR для каждого абонента, зарегистрированного в системе.
     *
     * @param start начальная дата и время
     * @param end конечная дата и время
     * @return список карт с данными UDR для всех абонентов
     */
    public List<Map<String, Object>> getUdrForAllSubscribers(OffsetDateTime start, OffsetDateTime end) {
        List<String> msisdns = subscriberRepository.findAll().stream().map(s -> s.getMsisdn()).toList();
        return msisdns.stream().map(msisdn -> getUdrByMsisdn(msisdn, start, end)).toList();
    }
    /**
     * Создает структуру UDR для указанного MSISDN и времени звонков.
     *
     * @param msisdn номер телефона абонента
     * @param incomingSeconds общее время входящих звонков в секундах
     * @param outgoingSeconds общее время исходящих звонков в секундах
     * @return карта с данными UDR
     */
    private Map<String, Object> buildUdr(String msisdn, long incomingSeconds, long outgoingSeconds) {
        Map<String, Object> udr = new HashMap<>();
        udr.put("msisdn", msisdn);
        udr.put("incomingCall", Map.of("totalTime", formatDuration(incomingSeconds)));
        udr.put("outgoingCall", Map.of("totalTime", formatDuration(outgoingSeconds)));
        return udr;
    }
    /**
     * Форматирует длительность в секундах в строку формата "HH:MM:SS".
     *
     * @param seconds длительность в секундах
     * @return строка с отформатированной длительностью
     */
    private String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long second = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, second);
    }
}
