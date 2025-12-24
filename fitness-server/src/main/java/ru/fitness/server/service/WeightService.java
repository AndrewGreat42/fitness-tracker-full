package ru.fitness.server.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fitness.common.dto.WeightCreateRequest;
import ru.fitness.server.domain.entity.UserEntity;
import ru.fitness.server.domain.entity.WeightLogEntity;
import ru.fitness.server.repo.WeightLogRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WeightService {

    private final WeightLogRepository weights;
    private final UserService userService;

    public WeightService(WeightLogRepository weights, UserService userService) {
        this.weights = weights;
        this.userService = userService;
    }

    @Transactional
    public WeightLogEntity add(long userId, WeightCreateRequest req) {
        UserEntity user = userService.get(userId);

        WeightLogEntity w = new WeightLogEntity();
        w.setUser(user);
        w.setWeight(req.weight);
        if (req.loggedAt != null && !req.loggedAt.isBlank()) {
            w.setLoggedAt(LocalDateTime.parse(req.loggedAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        WeightLogEntity saved = weights.save(w);

        // чтобы профиль показывал актуальный вес
        user.setWeight(req.weight);

        return saved;
    }

    public List<WeightLogEntity> historyDays(long userId, int days) {
        int d = Math.max(1, Math.min(days, 365));
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.minusDays(d - 1L).atStartOfDay();
        LocalDateTime to = today.plusDays(1).atStartOfDay();
        return weights.findInRange(userId, from, to);
    }

    public WeightLogEntity last(long userId) {
        List<WeightLogEntity> list = weights.findRecent(userId, PageRequest.of(0, 1));
        return list.isEmpty() ? null : list.get(0);
    }
}
