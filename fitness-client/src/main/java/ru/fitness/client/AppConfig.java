package ru.fitness.client;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private final String baseUrl;

    public AppConfig() {
        Properties p = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("client.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (Exception ignored) {
        }
        this.baseUrl = p.getProperty("api.baseUrl", "http://localhost:8080").trim();
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
