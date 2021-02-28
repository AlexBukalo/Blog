package main.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    private long timestamp;

    private short active;

    private String title;

    private String[] tags;

    private String text;
}
