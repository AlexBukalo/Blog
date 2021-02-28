package main.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.patterns.IToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@Table(name = "captcha_codes")
@NoArgsConstructor
public class CaptchaCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private Timestamp time;

    @NotNull
    private String code;

    @NotNull
    private String secretCode;

    public CaptchaCode(Timestamp time, String code, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.time = time;
        this.code = code;
        this.secretCode = bCryptPasswordEncoder.encode(code);
    }
}
