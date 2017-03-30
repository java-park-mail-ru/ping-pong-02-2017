package sample.configuations;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import sample.encoding.EncoderServiceInterface;
import sample.encoding.WeakEncoderService;

@Profile("test")
@Configuration
public class TestConfiguratioon {
    @Bean
    @Primary
    public EncoderServiceInterface encoderServiceInterface() {
        return new WeakEncoderService();
    }
}
