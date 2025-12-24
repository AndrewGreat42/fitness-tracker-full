package ru.fitness.common.dto;

public class UserDto {
    public long id;
    public String email;
    public String name;
    public String gender; // "male" / "female" или произвольное значение
    public int age;
    public double weight;
    public double height;
    public double activityLevel;
    public String goal; // "loss" / "maintenance" / "gain" либо любой текст

    public UserDto() {}
}
