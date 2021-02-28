package main.service.dto;

import lombok.Data;
import java.util.Map;

@Data
public class Calendar {

    private int[] years;

    private Map<String, Integer> posts;

    public Calendar(int[] years, Map<String, Integer> posts) {
        this.years = years;
        this.posts = posts;
    }
}
