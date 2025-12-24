package ru.fitness.client.api;

import ru.fitness.common.dto.KbjuTargetsDto;

public class NutritionApi {
    private final ApiClient api;

    public NutritionApi(ApiClient api) {
        this.api = api;
    }

    public KbjuTargetsDto targets() {
        return api.get("/api/nutrition/targets", KbjuTargetsDto.class);
    }
}
