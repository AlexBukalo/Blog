package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsRequest {

    @JsonProperty("MULTIUSER_MODE")
    private String multiuserMode;

    @JsonProperty("POST_PREMODERATION")
    private String postPremoderation;

    @JsonProperty("STATISTICS_IS_PUBLIC")
    private String statisticsIsPublic;

}
