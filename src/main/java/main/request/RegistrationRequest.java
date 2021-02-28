package main.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class RegistrationRequest {

    @JsonProperty("e_mail")
    private String email;

    private String name;

    private String password;

    private String captcha;

    @JsonProperty("captcha_secret")
    private String captchaSecret;

    private String code;

}
