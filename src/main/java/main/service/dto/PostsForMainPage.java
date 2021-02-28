package main.service.dto;

import lombok.Data;
import main.model.Post;


@Data
public class PostsForMainPage {
    private long id;

    private long timestamp;

    private UserDto user;

    private String title;

    private String announce;

    private long likeCount;

    private long dislikeCount;

    private long commentCount;

    private int viewCount;

    public PostsForMainPage(Post post, int likeCount, int dislikeCount, int commentCount) {
        this.id = post.getId();
        this.timestamp = post.getTime().getTime() / 1000;
        this.user = new UserDto(post.getUser());
        this.title = post.getTitle();
        this.announce = post.getAnnounce();
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.commentCount = commentCount;
        this.viewCount = post.getViewCount();
    }
}
