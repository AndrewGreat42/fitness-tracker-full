package ru.fitness.client.api;

import ru.fitness.common.dto.MealCreateRequest;
import ru.fitness.common.dto.MealDto;
import ru.fitness.common.dto.MealSummaryDto;

import java.util.Arrays;
import java.util.List;

public class MealApi {
    private final ApiClient api;

    public MealApi(ApiClient api) {
        this.api = api;
    }

    public MealDto add(MealCreateRequest req) {
        return api.post("/api/meals", req, MealDto.class);
    }

    public List<MealDto> recent(int limit) {
        MealDto[] arr = api.get("/api/meals/recent?limit=" + limit, MealDto[].class);
        return arr == null ? List.of() : Arrays.asList(arr);
    }

    public MealSummaryDto todaySummary() {
        return api.get("/api/meals/today/summary", MealSummaryDto.class);
    }
}
