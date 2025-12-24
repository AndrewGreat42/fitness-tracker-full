package ru.fitness.server.service;

import org.springframework.stereotype.Service;
import ru.fitness.common.dto.KbjuTargetsDto;
import ru.fitness.server.domain.entity.UserEntity;

@Service
public class NutritionService {

    public KbjuTargetsDto targets(UserEntity user) {
        // BMR (Mifflin-St Jeor)
        double bmr;
        String g = safeLower(user.getGender());
        if (g.contains("female") || g.contains("ж") || g.contains("f")) {
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() - 161;
        } else {
            bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5;
        }

        double tdee = bmr * user.getActivityLevel();

        // корректировка по цели
        String goal = safeLower(user.getGoal());
        double calories = tdee;
        if (goal.contains("loss") || goal.contains("пох") || goal.contains("суш")) {
            calories = tdee * 0.85; // -15%
        } else if (goal.contains("gain") || goal.contains("набор") || goal.contains("mass")) {
            calories = tdee * 1.10; // +10%
        }

        // Макросы (простая и понятная логика для курса)
        double proteinPerKg;
        if (goal.contains("loss")) proteinPerKg = 2.0;
        else if (goal.contains("gain")) proteinPerKg = 1.7;
        else proteinPerKg = 1.8;

        double protein = proteinPerKg * user.getWeight();
        double fat = 0.9 * user.getWeight();

        // калории -> в угли
        double calFromProtein = protein * 4.0;
        double calFromFat = fat * 9.0;
        double remaining = Math.max(0, calories - calFromProtein - calFromFat);
        double carbs = remaining / 4.0;

        KbjuTargetsDto dto = new KbjuTargetsDto();
        dto.calories = (int) Math.round(calories);
        dto.protein = round1(protein);
        dto.fat = round1(fat);
        dto.carbs = round1(carbs);
        return dto;
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.toLowerCase();
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
