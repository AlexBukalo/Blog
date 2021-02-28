package main.service.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Statistics {

    private int postsCount;

    private int likesCount;

    private int dislikesCount;

    private int viewsCount;

    private long firstPublication;
}
