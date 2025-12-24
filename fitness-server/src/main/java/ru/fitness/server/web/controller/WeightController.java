package ru.fitness.server.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import ru.fitness.common.dto.WeightCreateRequest;
import ru.fitness.common.dto.WeightEntryDto;
import ru.fitness.server.service.DtoMapper;
import ru.fitness.server.service.WeightService;
import ru.fitness.server.web.security.RequestAuth;

import java.util.List;

@RestController
@RequestMapping("/api/weights")
public class WeightController {

    private final WeightService weights;

    public WeightController(WeightService weights) {
        this.weights = weights;
    }

    @PostMapping
    public WeightEntryDto add(HttpServletRequest request, @RequestBody WeightCreateRequest req) {
        long userId = RequestAuth.userId(request);
        return DtoMapper.weight(weights.add(userId, req));
    }

    @GetMapping("/last")
    public WeightEntryDto last(HttpServletRequest request) {
        long userId = RequestAuth.userId(request);
        var w = weights.last(userId);
        return w == null ? null : DtoMapper.weight(w);
    }

    @GetMapping("/history")
    public List<WeightEntryDto> history(HttpServletRequest request,
                                        @RequestParam(name = "days", defaultValue = "7") int days) {
        long userId = RequestAuth.userId(request);
        return weights.historyDays(userId, days).stream().map(DtoMapper::weight).toList();
    }
}
