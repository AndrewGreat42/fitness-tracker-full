package ru.fitness.server.service;

import ru.fitness.common.dto.MealDto;
import ru.fitness.common.dto.UserDto;
import ru.fitness.common.dto.WeightEntryDto;
import ru.fitness.server.domain.entity.MealEntity;
import ru.fitness.server.domain.entity.UserEntity;
import ru.fitness.server.domain.entity.WeightLogEntity;

import java.time.format.DateTimeFormatter;

public final class DtoMapper {
    private DtoMapper() {}

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static UserDto user(UserEntity u) {
        UserDto dto = new UserDto();
        dto.id = u.getId();
        dto.email = u.getEmail();
        dto.name = u.getName();
        dto.gender = u.getGender();
        dto.age = u.getAge();
        dto.weight = u.getWeight();
        dto.height = u.getHeight();
        dto.activityLevel = u.getActivityLevel();
        dto.goal = u.getGoal();
        return dto;
    }

    public static MealDto meal(MealEntity m) {
        MealDto dto = new MealDto();
        dto.id = m.getId();
        dto.userId = m.getUser().getId();
        dto.description = m.getDescription();
        dto.calories = m.getCalories();
        dto.protein = m.getProtein();
        dto.fat = m.getFat();
        dto.carbs = m.getCarbs();
        dto.eatenAt = m.getEatenAt().format(ISO);
        return dto;
    }

    public static WeightEntryDto weight(WeightLogEntity w) {
        WeightEntryDto dto = new WeightEntryDto();
        dto.id = w.getId();
        dto.userId = w.getUser().getId();
        dto.weight = w.getWeight();
        dto.loggedAt = w.getLoggedAt().format(ISO);
        return dto;
    }
}
