package ru.fitness.client.session;

import ru.fitness.common.dto.UserDto;

public class SessionStore {
    private volatile String token;
    private volatile UserDto user;

    public String getToken() { return token; }
    public UserDto getUser() { return user; }

    public void set(String token, UserDto user) {
        this.token = token;
        this.user = user;
    }

    public void clear() {
        token = null;
        user = null;
    }
}
