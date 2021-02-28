package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("post_id")
    private long postId;

    private String text;
}
