package com.nexign.bootcamp.services;

import com.nexign.bootcamp.entities.Cdr;
import com.nexign.bootcamp.entities.Subscriber;
import com.nexign.bootcamp.repos.CdrRepository;
import com.nexign.bootcamp.repos.SubscriberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Сервис для генерации CDR-записей.
 * Автоматически создает тестовых абонентов и генерирует случайные CDR-записи для них при запуске приложения.
 */
@Service
@AllArgsConstructor
public class CdrGeneratorService {

    private final CdrRepository cdrRepository;
    private final SubscriberRepository subscriberRepository;
    private final Random random = new Random();

    /**
     * Инициализирует 10 тестовых абонентов и генерирует CDR-записи при запуске приложения.
     */
    @PostConstruct
    public void init() {
        List<Subscriber> subscribers = IntStream.range(1, 11)
                .mapToObj(i -> {
                    Subscriber subscriber = new Subscriber();
                    subscriber.setMsisdn("7999" + String.format("%06d", i));
                    return subscriber;
                }).collect(Collectors.toList());
        subscriberRepository.saveAll(subscribers);
        generateCdrForYear();
    }

    /**
     * Генерирует случайные CDR-записи для всех абонентов за год.
     */
    private void generateCdrForYear() {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        OffsetDateTime startOfYear = OffsetDateTime.parse("2025-01-01T00:00:00Z");
        OffsetDateTime endOfYear = startOfYear.plusYears(1);

        int callsCount = random.nextInt(500) + 500;
        for (int i = 0; i < callsCount; i++) {
            Cdr cdr = new Cdr();
            cdr.setCallType(random.nextBoolean() ? "01" : "02");
            Subscriber caller = subscribers.get(random.nextInt(subscribers.size()));
            Subscriber receiver = subscribers.get(random.nextInt(subscribers.size()));
            while (receiver.getMsisdn().equals(caller.getMsisdn())) {
                receiver = subscribers.get(random.nextInt(subscribers.size()));
            }
            cdr.setCallerMsisdn(caller.getMsisdn());
            cdr.setReceiverMsisdn(receiver.getMsisdn());

            long minutesInYear = ChronoUnit.MINUTES.between(startOfYear, endOfYear);
            long randomMinute = random.nextLong(minutesInYear);
            OffsetDateTime startTime = startOfYear.plusMinutes(randomMinute);
            int duration = random.nextInt(300) + 1;
            OffsetDateTime endTime = startTime.plusMinutes(duration);

            cdr.setStartTime(startTime);
            cdr.setEndTime(endTime);

            cdrRepository.save(cdr);
        }
    }
}