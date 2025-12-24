package ru.fitness.server.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import ru.fitness.common.dto.KbjuTargetsDto;
import ru.fitness.server.service.NutritionService;
import ru.fitness.server.service.UserService;
import ru.fitness.server.web.security.RequestAuth;

@RestController
@RequestMapping("/api/nutrition")
public class NutritionController {

    private final UserService users;
    private final NutritionService nutrition;

    public NutritionController(UserService users, NutritionService nutrition) {
        this.users = users;
        this.nutrition = nutrition;
    }

    @GetMapping("/targets")
    public KbjuTargetsDto targets(HttpServletRequest request) {
        long userId = RequestAuth.userId(request);
        return nutrition.targets(users.get(userId));
    }
}
