package sample.encoding;

import org.springframework.security.crypto.password.PasswordEncoder;


public interface EncoderServiceInterface {
    PasswordEncoder getPasswordEncoder();
}
