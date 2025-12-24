package ru.fitness.server.web.security;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestAuth {
    private RequestAuth() {}

    public static long userId(HttpServletRequest request) {
        Object v = request.getAttribute(AuthInterceptor.ATTR_USER_ID);
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        throw new IllegalStateException("User ID not set");
    }
}
