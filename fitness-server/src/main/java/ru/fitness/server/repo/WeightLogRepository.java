package ru.fitness.server.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.fitness.server.domain.entity.WeightLogEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface WeightLogRepository extends JpaRepository<WeightLogEntity, Long> {

    @Query("select w from WeightLogEntity w where w.user.id = :userId order by w.loggedAt desc")
    List<WeightLogEntity> findRecent(@Param("userId") long userId, Pageable pageable);

    @Query("select w from WeightLogEntity w where w.user.id = :userId and w.loggedAt >= :from and w.loggedAt < :to order by w.loggedAt asc")
    List<WeightLogEntity> findInRange(@Param("userId") long userId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
