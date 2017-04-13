package sample.encoding;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;


public class WeakEncoderService implements EncoderServiceInterface {
    @Override
    public PasswordEncoder getPasswordEncoder() {
        return PASSWORD_ENCODER;
    }

    private static final PasswordEncoder PASSWORD_ENCODER = new StandardPasswordEncoder();
}
