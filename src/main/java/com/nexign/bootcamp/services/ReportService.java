package com.nexign.bootcamp.services;

import com.nexign.bootcamp.entities.Cdr;
import com.nexign.bootcamp.repos.CdrRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для генерации отчетов по CDR (Call Detail Records).
 * Отвечает за создание CSV-файлов с данными о звонках для указанного абонента и временного диапазона.
 */
@Service
@AllArgsConstructor
public class ReportService {

    private final CdrRepository cdrRepository;

    private static final String REPORTS_DIR = "./";

    /**
     * Генерирует отчет CDR для указанного абонента и временного диапазона.
     * Запрашивает данные из репозитория, проверяет их наличие и создает CSV-файл с результатами.
     *
     * @param msisdn номер телефона абонента (MSISDN)
     * @param start начальная дата и время диапазона
     * @param end конечная дата и время диапазона
     * @return строка с UUID сгенерированного файла
     * @throws IOException если произошла ошибка при записи файла
     * @throws IllegalArgumentException если записи CDR для указанного диапазона не найдены
     */
    public String generateCdrReport(String msisdn, OffsetDateTime start, OffsetDateTime end) throws IOException {
        List<Cdr> cdrs = cdrRepository.findByCallerMsisdnAndStartTimeBetween(msisdn, start, end);
        if (cdrs.isEmpty()) {
            throw new IllegalArgumentException("No CDR records found for the given MSISDN and time range.");
        }

        String uuid = UUID.randomUUID().toString();
        String fileName = REPORTS_DIR + msisdn + "_" + uuid + ".csv";

        try (FileWriter writer = new FileWriter(fileName)) {
            for (Cdr cdr : cdrs) {
                writer.write(String.format("%s,%s,%s,%s,%s\n",
                        cdr.getCallType(),
                        cdr.getCallerMsisdn(),
                        cdr.getReceiverMsisdn(),
                        cdr.getStartTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        cdr.getEndTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
            }
        }
        return uuid;
    }
}