package main.service.dto;

import lombok.Data;


import java.util.List;

@Data
public class CountPosts {

    private long count;

    private List<PostsForMainPage> posts;

    public CountPosts(long count, List<PostsForMainPage> posts) {
        this.count = count;
        this.posts = posts;
    }
}
