package ru.fitness.client;

import ru.fitness.client.api.*;
import ru.fitness.client.session.SessionStore;

public class AppContext {
    public final SessionStore session;
    public final ApiClient apiClient;
    public final AuthApi auth;
    public final ProfileApi profile;
    public final MealApi meals;
    public final WeightApi weights;
    public final NutritionApi nutrition;

    public AppContext(AppConfig config) {
        this.session = new SessionStore();
        this.apiClient = new ApiClient(config, session);
        this.auth = new AuthApi(apiClient);
        this.profile = new ProfileApi(apiClient);
        this.meals = new MealApi(apiClient);
        this.weights = new WeightApi(apiClient);
        this.nutrition = new NutritionApi(apiClient);
    }
}
