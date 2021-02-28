package main.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.model.GlobalSettings;

import java.util.List;

@Data
public class Settings {

    @JsonProperty("MULTIUSER_MODE")
    private boolean MULTIUSER_MODE;

    @JsonProperty("POST_PREMODERATION")
    private boolean POST_PREMODERATION;

    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean STATISTICS_IS_PUBLIC;

    public Settings(List<GlobalSettings> globalSettings) {
        for (GlobalSettings settings : globalSettings) {
            if (settings.getCode().equals("MULTIUSER_MODE") && settings.getValue().equals("YES")) this.MULTIUSER_MODE = true;
            if (settings.getCode().equals("POST_PREMODERATION") &&  settings.getValue().equals("YES")) this.POST_PREMODERATION = true;
            if (settings.getCode().equals("STATISTICS_IS_PUBLIC") && settings.getValue().equals("YES")) this.STATISTICS_IS_PUBLIC = true;
        }
    }
}
