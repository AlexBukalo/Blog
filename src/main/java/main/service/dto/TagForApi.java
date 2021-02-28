package main.service.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TagForApi {
    private List<TagDto> tags = new ArrayList<>();

    public TagForApi(List<TagDto> tags) {
        this.tags = tags;
    }
}
