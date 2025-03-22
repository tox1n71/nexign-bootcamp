package com.nexign.bootcamp.repos;
import com.nexign.bootcamp.entities.Cdr;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Репозиторий для получения из базы данных CDR записей
 */
public interface CdrRepository extends JpaRepository<Cdr, Long> {
    /**
     * Метод для получения списка исходящих звонков абонента за заданный период
     * @param msisdn
     * @param start
     * @param end
     * @return List<Cdr>
     */
    List<Cdr> findByCallerMsisdnAndStartTimeBetween(String msisdn, OffsetDateTime start, OffsetDateTime end);
    /**
     * Метод для получения списка входящих звонков абонента за заданный период
     * @param msisdn
     * @param start
     * @param end
     * @return List<Cdr>
     */
    List<Cdr> findByReceiverMsisdnAndStartTimeBetween(String msisdn, OffsetDateTime start, OffsetDateTime end);
    List<Cdr> findByStartTimeBetween(OffsetDateTime start, OffsetDateTime end);
}
