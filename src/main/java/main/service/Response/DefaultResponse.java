package main.service.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.service.dto.Errors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefaultResponse {

    private boolean result = true;

    private Errors errors;
}
