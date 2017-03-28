package sample.services.account;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sample.UserProfile;

import java.util.*;

@Service("AccountServiceDB")
public class AccountServiceDB implements AccountServiceInterface {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserProfile register(@NotNull String email, @NotNull String login, @NotNull String password) {
        return null;
    }

    @Override
    public boolean login(@NotNull String email, @NotNull String password) {
        return false;
    }

    @Override
    public UserProfile getUser(@NotNull String email) {
        return null;
    }

    @Override
    public UserProfile update(@NotNull UserProfile userProfile, @NotNull UserProfile changedProfile) {
        return null;
    }

    @Override
    public void updateScore(@NotNull UserProfile userProfile) {

    }

    @Override
    public ArrayList<UserProfile> getSortedUsersByScore() {
        return null;
    }

    @Override
    public void flush() {

    }
}
