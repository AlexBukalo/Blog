package main.service.dto;

import lombok.Data;
import main.model.Post;

import java.util.List;

@Data
public class PostForId {
    private long id;

    private long timestamp;

    private boolean active;

    private UserDto user;

    private String title;

    private String text;

    private String announce;

    private long likeCount;

    private long dislikeCount;

    private int viewCount;

    private List<CommentDto> comments;

    private String[] tags;

    public PostForId(Post post, int likeCount, int dislikeCount, List<CommentDto> comments, String[] tags) {
        this.id = post.getId();
        this.timestamp = post.getTime().getTime() / 1000;
        this.active = post.getIsActive() == 1;
        this.user = new UserDto(post.getUser());
        this.title = post.getTitle();
        this.text = post.getText();
        this.announce = post.getAnnounce();
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.viewCount = post.getViewCount();
        this.comments = comments;
        this.tags = tags;
    }
}
