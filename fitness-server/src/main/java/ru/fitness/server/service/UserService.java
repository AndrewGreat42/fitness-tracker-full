package ru.fitness.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fitness.common.dto.UserUpdateRequest;
import ru.fitness.server.domain.entity.UserEntity;
import ru.fitness.server.repo.UserRepository;

@Service
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public UserEntity get(long userId) {
        return users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public UserEntity update(long userId, UserUpdateRequest req) {
        UserEntity u = get(userId);

        if (req.email != null && !req.email.isBlank()) u.setEmail(req.email.trim());
        if (req.name != null && !req.name.isBlank()) u.setName(req.name.trim());
        if (req.gender != null && !req.gender.isBlank()) u.setGender(req.gender.trim());
        if (req.age > 0) u.setAge(req.age);
        if (req.weight > 0) u.setWeight(req.weight);
        if (req.height > 0) u.setHeight(req.height);
        if (req.activityLevel > 0) u.setActivityLevel(req.activityLevel);
        if (req.goal != null && !req.goal.isBlank()) u.setGoal(req.goal.trim());

        return users.save(u);
    }
}
