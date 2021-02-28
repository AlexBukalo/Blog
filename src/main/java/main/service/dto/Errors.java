package main.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Errors {

    private String email;

    private String name;

    private String password;

    private String captcha;

    private String title;

    private String text;

    private String image;

    private String photo;

    private String code;
}
