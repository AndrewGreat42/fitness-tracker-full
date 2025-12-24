package ru.fitness.client.api;

import ru.fitness.common.dto.AuthLoginRequest;
import ru.fitness.common.dto.AuthRegisterRequest;
import ru.fitness.common.dto.AuthResponse;

public class AuthApi {
    private final ApiClient api;

    public AuthApi(ApiClient api) {
        this.api = api;
    }

    public AuthResponse login(String email, String password) {
        AuthLoginRequest req = new AuthLoginRequest();
        req.email = email;
        req.password = password;
        return api.post("/api/auth/login", req, AuthResponse.class);
    }

    public AuthResponse register(AuthRegisterRequest req) {
        return api.post("/api/auth/register", req, AuthResponse.class);
    }
}
