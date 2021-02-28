package main.service.Response;

import lombok.Data;
import main.model.User;

@Data
public class UserLoginResponse {
    private int id;

    private String name;

    private String photo;

    private String email;

    private boolean moderation;

    private int moderationCount;

    private boolean settings;

    public UserLoginResponse(User user, int moderationCount) {
        this.id = user.getId();
        this.name = user.getName();
        this.photo = user.getPhoto();
        this.email = user.getEmail();
        if (user.getIsModerator() == 1) {
            this.moderation = true;
            this.settings = true;
            this.moderationCount = moderationCount;
        } else {
            this.moderation = false;
            this.moderationCount = 0;
            this.settings = false;
        }
    }

}
