package ru.fitness.server.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.fitness.server.domain.entity.SessionEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
    Optional<SessionEntity> findByToken(String token);
    long deleteByExpiresAtBefore(LocalDateTime time);
}
