package ru.fitness.server.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.fitness.server.domain.entity.MealEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface MealRepository extends JpaRepository<MealEntity, Long> {

    @Query("select m from MealEntity m where m.user.id = :userId and m.eatenAt >= :from and m.eatenAt < :to order by m.eatenAt desc")
    List<MealEntity> findInRange(@Param("userId") long userId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select m from MealEntity m where m.user.id = :userId order by m.eatenAt desc")
    List<MealEntity> findRecent(@Param("userId") long userId, Pageable pageable);
}
