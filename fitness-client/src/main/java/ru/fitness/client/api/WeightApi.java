package ru.fitness.client.api;

import ru.fitness.common.dto.WeightCreateRequest;
import ru.fitness.common.dto.WeightEntryDto;

import java.util.Arrays;
import java.util.List;

public class WeightApi {
    private final ApiClient api;

    public WeightApi(ApiClient api) {
        this.api = api;
    }

    public WeightEntryDto add(double weight) {
        WeightCreateRequest req = new WeightCreateRequest();
        req.weight = weight;
        req.loggedAt = null;
        return api.post("/api/weights", req, WeightEntryDto.class);
    }

    public WeightEntryDto last() {
        return api.get("/api/weights/last", WeightEntryDto.class);
    }

    public List<WeightEntryDto> history(int days) {
        WeightEntryDto[] arr = api.get("/api/weights/history?days=" + days, WeightEntryDto[].class);
        return arr == null ? List.of() : Arrays.asList(arr);
    }
}
