package ru.fitness.common.dto;

public class MealCreateRequest {
    public String description;
    public int calories;
    public double protein;
    public double fat;
    public double carbs;

    public String eatenAt;

    public MealCreateRequest() {}
}
