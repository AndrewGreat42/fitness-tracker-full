package ru.fitness.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.fitness.client.AppConfig;
import ru.fitness.client.session.SessionStore;
import ru.fitness.common.dto.ApiError;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class ApiClient {


    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper om;
    private final String baseUrl;
    private final SessionStore session;

    public ApiClient(AppConfig config, SessionStore session) {
        this.baseUrl = config.getBaseUrl();
        this.session = session;
        this.om = new ObjectMapper();
        this.om.registerModule(new JavaTimeModule());
    }

    public <T> T get(String path, Class<T> responseType) {
        return send("GET", path, null, responseType);
    }

    public <T> T post(String path, Object body, Class<T> responseType) {
        return send("POST", path, body, responseType);
    }

    public <T> T put(String path, Object body, Class<T> responseType) {
        return send("PUT", path, body, responseType);
    }

    private <T> T send(String method, String path, Object body, Class<T> responseType) {
        try {
            String url = baseUrl + path;
            HttpRequest.Builder b = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json");


            b.timeout(Duration.ofSeconds(10));

            if (session.getToken() != null) {
                b.header("Authorization", "Bearer " + session.getToken());
            }

            if (body != null) {
                String json = om.writeValueAsString(body);
                b.header("Content-Type", "application/json; charset=utf-8");
                b.method(method, HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));
            } else {
                b.method(method, HttpRequest.BodyPublishers.noBody());
            }

            HttpResponse<String> resp = http.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            int code = resp.statusCode();
            String payload = resp.body() == null ? "" : resp.body();

            if (code >= 200 && code < 300) {
                if (responseType == Void.class) return null;
                if (payload.isBlank()) return null;
                return om.readValue(payload, responseType);
            }


            String msg = "HTTP " + code;
            try {
                ApiError err = om.readValue(payload, ApiError.class);
                if (err != null && err.message != null && !err.message.isBlank()) {
                    msg = err.message;
                }
            } catch (Exception ignored) {
            }
            throw new ApiException(code, msg);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(0, "Не удалось подключиться к серверу: " + e.getMessage());
        }
    }
}
