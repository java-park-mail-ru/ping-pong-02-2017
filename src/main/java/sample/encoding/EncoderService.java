package sample.encoding;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class EncoderService implements EncoderServiceInterface {
    @Override
    public PasswordEncoder getPasswordEncoder() {
        return PASSWORD_ENCODER;
    }

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
}
