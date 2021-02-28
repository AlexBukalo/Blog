package main.service.dto;

import lombok.Data;

@Data
public class TagDto {
    private String name;

    private double weight;

    public TagDto(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }
}
