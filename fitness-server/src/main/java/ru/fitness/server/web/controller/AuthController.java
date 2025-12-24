package ru.fitness.server.web.controller;

import org.springframework.web.bind.annotation.*;
import ru.fitness.common.dto.AuthLoginRequest;
import ru.fitness.common.dto.AuthRegisterRequest;
import ru.fitness.common.dto.AuthResponse;
import ru.fitness.server.service.AuthService;

@RestController
@RequestMapping("/api/auth")    
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRegisterRequest req) {
        return auth.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthLoginRequest req) {
        return auth.login(req);
    }
}
