package ru.fitness.common.dto;

public class ApiError {
    public String message;
    public String details;

    public ApiError() {}

    public ApiError(String message, String details) {
        this.message = message;
        this.details = details;
    }
}
