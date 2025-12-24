package ru.fitness.client.api;

import ru.fitness.common.dto.UserDto;
import ru.fitness.common.dto.UserUpdateRequest;

public class ProfileApi {
    private final ApiClient api;

    public ProfileApi(ApiClient api) {
        this.api = api;
    }

    public UserDto me() {
        return api.get("/api/profile/me", UserDto.class);
    }

    public UserDto updateMe(UserUpdateRequest req) {
        return api.put("/api/profile/me", req, UserDto.class);
    }
}
