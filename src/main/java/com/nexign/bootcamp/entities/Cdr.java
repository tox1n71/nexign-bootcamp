package com.nexign.bootcamp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Сущность, представляющая запись о звонке (Call Detail Record, CDR).
 * Хранит информацию о типе звонка, номерах звонящего и получателя, а также времени начала и окончания звонка.
 */
@Entity
@Data
@Getter
@Setter
public class Cdr {

    /**
     * Уникальный идентификатор записи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Тип звонка: "01" для исходящего, "02" для входящего.
     */
    private String callType;

    /**
     * Номер телефона звонящего (MSISDN).
     */
    private String callerMsisdn;

    /**
     * Номер телефона получателя (MSISDN).
     */
    private String receiverMsisdn;

    /**
     * Время начала звонка в формате ISO-8601.
     */
    private OffsetDateTime startTime;

    /**
     * Время окончания звонка в формате ISO-8601.
     */
    private OffsetDateTime endTime;
}