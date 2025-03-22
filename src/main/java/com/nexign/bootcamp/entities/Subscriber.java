package com.nexign.bootcamp.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

/**
 * Сущность абонента.
 */
@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subscriber {
    @Id
    /**
     * Уникальный номер абонента
     */
    private String msisdn;
}