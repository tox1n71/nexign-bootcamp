package com.nexign.bootcamp.repos;
import com.nexign.bootcamp.entities.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для получения Абонентов из базы данных
 */
public interface SubscriberRepository extends JpaRepository<Subscriber, String> {
}