package ru.fitness.server.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fitness.common.dto.MealCreateRequest;
import ru.fitness.common.dto.MealSummaryDto;
import ru.fitness.server.domain.entity.MealEntity;
import ru.fitness.server.domain.entity.UserEntity;
import ru.fitness.server.repo.MealRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MealService {

    private final MealRepository meals;
    private final UserService userService;

    public MealService(MealRepository meals, UserService userService) {
        this.meals = meals;
        this.userService = userService;
    }

    @Transactional
    public MealEntity addMeal(long userId, MealCreateRequest req) {
        UserEntity user = userService.get(userId);
        MealEntity m = new MealEntity();
        m.setUser(user);
        m.setDescription(req.description == null ? "" : req.description.trim());
        m.setCalories(req.calories);
        m.setProtein(req.protein);
        m.setFat(req.fat);
        m.setCarbs(req.carbs);
        if (req.eatenAt != null && !req.eatenAt.isBlank()) {
            m.setEatenAt(LocalDateTime.parse(req.eatenAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        return meals.save(m);
    }

    public List<MealEntity> recent(long userId, int limit) {
        return meals.findRecent(userId, PageRequest.of(0, Math.max(1, Math.min(limit, 50))));
    }

    public MealSummaryDto todaySummary(long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = today.plusDays(1).atStartOfDay();
        List<MealEntity> list = meals.findInRange(userId, from, to);

        MealSummaryDto s = new MealSummaryDto();
        for (MealEntity m : list) {
            s.calories += m.getCalories();
            s.protein += m.getProtein();
            s.fat += m.getFat();
            s.carbs += m.getCarbs();
        }
        // округление для красоты
        s.protein = round1(s.protein);
        s.fat = round1(s.fat);
        s.carbs = round1(s.carbs);
        return s;
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
