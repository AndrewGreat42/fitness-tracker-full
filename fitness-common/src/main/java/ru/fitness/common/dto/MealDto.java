package ru.fitness.common.dto;

public class MealDto {
    public long id;
    public long userId;
    public String description;
    public int calories;
    public double protein;
    public double fat;
    public double carbs;
    public String eatenAt; // ISO

    public MealDto() {}
}
