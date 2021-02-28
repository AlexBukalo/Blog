package main.service.dto;

import lombok.Data;
import main.model.User;

@Data
public class UserDto {
    private int id;

    private String name;

    private String photo;

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.photo = user.getPhoto();
    }
}
