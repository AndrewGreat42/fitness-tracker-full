package ru.fitness.server.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import ru.fitness.common.dto.MealCreateRequest;
import ru.fitness.common.dto.MealDto;
import ru.fitness.common.dto.MealSummaryDto;
import ru.fitness.server.service.DtoMapper;
import ru.fitness.server.service.MealService;
import ru.fitness.server.web.security.RequestAuth;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService meals;

    public MealController(MealService meals) {
        this.meals = meals;
    }

    @PostMapping
    public MealDto add(HttpServletRequest request, @RequestBody MealCreateRequest req) {
        long userId = RequestAuth.userId(request);
        return DtoMapper.meal(meals.addMeal(userId, req));
    }

    @GetMapping("/recent")
    public List<MealDto> recent(HttpServletRequest request,
                                @RequestParam(name = "limit", defaultValue = "5") int limit) {
        long userId = RequestAuth.userId(request);
        return meals.recent(userId, limit).stream().map(DtoMapper::meal).toList();
    }

    @GetMapping("/today/summary")
    public MealSummaryDto todaySummary(HttpServletRequest request) {
        long userId = RequestAuth.userId(request);
        return meals.todaySummary(userId);
    }
}
