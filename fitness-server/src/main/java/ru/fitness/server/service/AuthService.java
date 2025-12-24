package ru.fitness.server.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fitness.common.dto.AuthLoginRequest;
import ru.fitness.common.dto.AuthRegisterRequest;
import ru.fitness.common.dto.AuthResponse;
import ru.fitness.server.domain.entity.SessionEntity;
import ru.fitness.server.domain.entity.UserEntity;
import ru.fitness.server.repo.SessionRepository;
import ru.fitness.server.repo.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class AuthService {

    private final UserRepository users;
    private final SessionRepository sessions;
    private final WeightService weightService;

    @Value("${app.auth.session-days:30}")
    private int sessionDays;

    private final SecureRandom random = new SecureRandom();

    public AuthService(UserRepository users, SessionRepository sessions, WeightService weightService) {
        this.users = users;
        this.sessions = sessions;
        this.weightService = weightService;
    }

    @Transactional
    public AuthResponse register(AuthRegisterRequest req) {
        if (req.email == null || req.email.isBlank()) throw new IllegalArgumentException("Email обязателен");
        if (req.password == null || req.password.isBlank()) throw new IllegalArgumentException("Пароль обязателен");
        if (users.findByEmail(req.email.trim()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        UserEntity u = new UserEntity();
        u.setEmail(req.email.trim());
        u.setPasswordHash(BCrypt.hashpw(req.password, BCrypt.gensalt()));
        u.setName(defaultIfBlank(req.name, "User"));
        u.setGender(defaultIfBlank(req.gender, "male"));
        u.setAge(req.age);
        u.setWeight(req.weight);
        u.setHeight(req.height);
        u.setActivityLevel(req.activityLevel);
        u.setGoal(defaultIfBlank(req.goal, "maintenance"));

        users.save(u);

        // первая запись веса
        if (req.weight > 0) {
            var wreq = new ru.fitness.common.dto.WeightCreateRequest();
            wreq.weight = req.weight;
            wreq.loggedAt = null;
            weightService.add(u.getId(), wreq);
        }

        return loginInternal(u);
    }

    @Transactional
    public AuthResponse login(AuthLoginRequest req) {
        if (req.email == null || req.email.isBlank()) throw new IllegalArgumentException("Email обязателен");
        if (req.password == null || req.password.isBlank()) throw new IllegalArgumentException("Пароль обязателен");

        UserEntity u = users.findByEmail(req.email.trim()).orElseThrow(() -> new IllegalArgumentException("Неверный логин или пароль"));
        if (!BCrypt.checkpw(req.password, u.getPasswordHash())) {
            throw new IllegalArgumentException("Неверный логин или пароль");
        }

        return loginInternal(u);
    }

    private AuthResponse loginInternal(UserEntity u) {
        // чистим просроченные
        sessions.deleteByExpiresAtBefore(LocalDateTime.now());

        SessionEntity s = new SessionEntity();
        s.setUser(u);
        s.setToken(generateToken());
        s.setCreatedAt(LocalDateTime.now());
        s.setExpiresAt(LocalDateTime.now().plusDays(sessionDays));
        sessions.save(s);

        AuthResponse resp = new AuthResponse();
        resp.token = s.getToken();
        resp.user = DtoMapper.user(u);
        return resp;
    }

    private String generateToken() {
        byte[] buf = new byte[32];
        random.nextBytes(buf);
        return HexFormat.of().formatHex(buf);
    }

    private static String defaultIfBlank(String s, String def) {
        if (s == null) return def;
        String t = s.trim();
        return t.isEmpty() ? def : t;
    }
}
