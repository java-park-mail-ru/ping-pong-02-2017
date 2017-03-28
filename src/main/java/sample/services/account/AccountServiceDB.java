package sample.services.account;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sample.UserMapper;
import sample.UserProfile;

import java.util.ArrayList;

/**
 * Created by sergey on 26.03.17.
 */

@Service("AccountServiceDB")
public class AccountServiceDB implements AccountServiceInterface {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public UserProfile register(UserProfile userProfile) {
        try {
            jdbcTemplate.update("INSERT INTO \"User\" (login, email, password, score) values (?, ?, ?, ?) ",
                    userProfile.getLogin(), userProfile.getEmail(), userProfile.getPassword(), userProfile.getScore());
            return getUser(userProfile.getEmail());
        } catch (DataAccessException e) {
            return null;
        }

    }

    public boolean login(@NotNull String email, @NotNull String password) {
        try {
            UserProfile userProfile = jdbcTemplate.queryForObject("SELECT * FROM \"User\" WHERE email = ?::citext",
                    new Object[]{email}, new UserMapper());
            return passwordEncoder().matches(password, userProfile.getPassword());
        } catch (DataAccessException e) {
            return false;
        }
    }

    public UserProfile getUser(@NotNull String email) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM \"User\" WHERE email = ?::citext",
                    new Object[]{email}, new UserMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    public UserProfile update(@NotNull UserProfile userProfile, @NotNull UserProfile changedProfile) {
        if (!isEmptyField(changedProfile.getEmail())) {
            if (!changedProfile.getEmail().equals(userProfile.getEmail()) &&
                    getUser(changedProfile.getEmail()) != null) { //такой email занят другим пользователем
                return null;
            }
        }

        if (passwordEncoder().matches(changedProfile.getPassword(), userProfile.getPassword())) {
            changedProfile.setPassword("");
        }

        updateNotNullFields(userProfile, changedProfile);

        if(userProfile.getEmail().equals(changedProfile.getEmail())) {
            try {
                jdbcTemplate.update("DELETE FROM \"User\" WHERE email = ?::citext",userProfile.getEmail());
                jdbcTemplate.update("INSERT INTO \"User\" (login, email, password, score) values (?, ?, ?, ?) ",
                        userProfile.getLogin(), userProfile.getEmail(), userProfile.getPassword(), userProfile.getScore());
                return getUser(userProfile.getEmail());
            } catch (DataAccessException e) {
                return null;
            }
        }
        try {
            jdbcTemplate.update("INSERT INTO \"User\" (login, email, password, score) values (?, ?, ?, ?) ",
                    userProfile.getLogin(), userProfile.getEmail(), userProfile.getPassword(), userProfile.getScore());
            return getUser(userProfile.getEmail());
        } catch (DataAccessException e) {
            return null;
        }
    }

    public void updateScore(@NotNull UserProfile userProfile) {
        try {
            jdbcTemplate.update("UPDATE \"User\" SET score = ? WHERE email = ?::citext",
                    userProfile.getScore(), userProfile.getEmail());
        } catch (DataAccessException e) {
            return;
        }
    }

    public ArrayList<UserProfile> getSortedUsersByScore(int count) {
        try {
            return (ArrayList<UserProfile>) jdbcTemplate.query("SELECT * FROM \"User\" ORDER BY score DESC LIMIT ?",
                    new Object[]{count}, new UserMapper());
        } catch (DataAccessException e) {
            return null;
        }
    }

    public void flush() {

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
