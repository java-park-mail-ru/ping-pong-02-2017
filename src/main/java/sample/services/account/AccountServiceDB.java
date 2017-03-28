package sample.services.account;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sample.UserMapper;
import sample.UserProfile;

import java.util.List;

/**
 * Created by sergey on 26.03.17.
 */

@Service("AccountServiceDB")
public class AccountServiceDB implements AccountServiceInterface {
    final
    JdbcTemplate jdbcTemplate;

    @Autowired
    public AccountServiceDB(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Nullable
    @Override
    public UserProfile register(UserProfile userProfile) {
        try {
            jdbcTemplate.update("INSERT INTO \"User\" (login, email, password, score) values (?, ?, ?, ?) ",
                    userProfile.getLogin(), userProfile.getEmail(), userProfile.getPassword(), userProfile.getScore());
            return getUser(userProfile.getEmail());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public boolean login(@NotNull String email, @NotNull String password) {
        try {
            final UserProfile userProfile = jdbcTemplate.queryForObject("SELECT * FROM \"User\" WHERE LOWER(email) = LOWER(?)",
                    new Object[]{email}, new UserMapper());
            return passwordEncoder().matches(password, userProfile.getPassword());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    @Override
    public UserProfile getUser(@NotNull String email) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM \"User\" WHERE LOWER(email) = LOWER(?)",
                    new Object[]{email}, new UserMapper());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    @Override
    public UserProfile update(@NotNull String email, @NotNull UserProfile changedProfile) {
        UserProfile userProfile = getUser(email);
        if (userProfile == null) {
            return changedProfile;
        }

        if (passwordEncoder().matches(changedProfile.getPassword(), userProfile.getPassword())) {
            changedProfile.setPassword("");
        }

        updateNotNullFields(userProfile, changedProfile);

        try {
            jdbcTemplate.update("UPDATE \"User\" SET login = ?, email = ?, password = ?, score = ? WHERE id = ?",
                    userProfile.getLogin(), userProfile.getEmail(), userProfile.getPassword(), userProfile.getScore(), userProfile.getId());
            return getUser(userProfile.getEmail());
        } catch (DataAccessException e) {
            return null;
        }

    }

    @Nullable
    @Override
    public UserProfile updateScore(@NotNull UserProfile userProfile) {
        try {
            jdbcTemplate.update("UPDATE \"User\" SET score = ? WHERE LOWER(email) = LOWER(?)",
                    userProfile.getScore(), userProfile.getEmail());
            return getUser(userProfile.getEmail());
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<UserProfile> getSortedUsersByScore(int count) {
        try {
            return jdbcTemplate.query("SELECT * FROM \"User\" ORDER BY score DESC LIMIT ?",
                    new Object[]{count}, new UserMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    private boolean isEmptyField(String field) {
        return ((field == null) || field.isEmpty());
    }

    private void updateNotNullFields(@NotNull UserProfile userProfile, @NotNull UserProfile changedProfile) {
        if(!isEmptyField(changedProfile.getEmail())) {
            userProfile.setEmail(changedProfile.getEmail());
        }
        if(!isEmptyField(changedProfile.getLogin())) {
            userProfile.setLogin(changedProfile.getLogin());
        }
        if(!isEmptyField(changedProfile.getPassword())) {
            userProfile.setPassword(passwordEncoder().encode(changedProfile.getPassword()));
            System.out.println(userProfile.getPassword());
        }
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
