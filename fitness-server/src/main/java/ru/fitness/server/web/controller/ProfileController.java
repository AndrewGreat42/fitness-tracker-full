package ru.fitness.server.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import ru.fitness.common.dto.UserDto;
import ru.fitness.common.dto.UserUpdateRequest;
import ru.fitness.server.service.DtoMapper;
import ru.fitness.server.service.UserService;
import ru.fitness.server.web.security.RequestAuth;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService users;

    public ProfileController(UserService users) {
        this.users = users;
    }

    @GetMapping("/me")
    public UserDto me(HttpServletRequest request) {
        long userId = RequestAuth.userId(request);
        return DtoMapper.user(users.get(userId));
    }

    @PutMapping("/me")
    public UserDto update(HttpServletRequest request, @RequestBody UserUpdateRequest req) {
        long userId = RequestAuth.userId(request);
        return DtoMapper.user(users.update(userId, req));
    }
}
