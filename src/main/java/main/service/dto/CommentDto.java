package main.service.dto;

import lombok.Data;
import main.model.PostComment;

@Data
public class CommentDto {

    private int id;

    private long timestamp;

    private String text;

    private UserDto user;

    public CommentDto(PostComment postComment) {
        this.id = postComment.getId();
        this.timestamp = postComment.getTime().getTime() / 1000;
        this.text = postComment.getText();
        this.user = new UserDto(postComment.getUser());
    }
}
