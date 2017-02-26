package sample;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by sergey on 23.02.17.
 */
public class UserProfile {
    private String email;
    private String password;
    private String login;
    private long id;

    public UserProfile(UserProfile userProfile) {
        this.email = userProfile.email;
        this.login = userProfile.login;
        this.password = userProfile.password;
    }

    public UserProfile(@JsonProperty("email") String email, @JsonProperty("login") String login,
                       @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
        this.login = login;
    }

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    public final void setId() {
        this.id = ID_GENERATOR.getAndIncrement();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    public Long getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

}
