package main.model.repository;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CaptchaRepository extends JpaRepository<CaptchaCode, Long> {

    @Transactional
    @Modifying
    @Query(value = "delete from captcha_codes where TIMESTAMPDIFF(hour, time, current_time())",
            nativeQuery = true)
    void deleteCaptcha();

    @Query("SELECT c FROM CaptchaCode c WHERE c.code = :code AND c.secretCode = :secretCode")
    Optional<CaptchaCode> getCaptcha(@Param("code") String code, @Param("secretCode") String secretCode);
}
