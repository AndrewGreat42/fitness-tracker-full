package ru.fitness.server.web.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.fitness.server.domain.entity.SessionEntity;
import ru.fitness.server.repo.SessionRepository;

import java.time.LocalDateTime;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATTR_USER_ID = "auth.userId";

    private final SessionRepository sessions;

    public AuthInterceptor(SessionRepository sessions) {
        this.sessions = sessions;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        String token = auth.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        SessionEntity session = sessions.findByToken(token).orElse(null);
        if (session == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            sessions.delete(session);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        request.setAttribute(ATTR_USER_ID, session.getUser().getId());
        return true;
    }
}
