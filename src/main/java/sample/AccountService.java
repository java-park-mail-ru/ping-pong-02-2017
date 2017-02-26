package sample;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergey on 23.02.17.
 */

@Service
public class AccountService {
    private Map<String, UserProfile> userNameToUserProfile = new HashMap<>();
    /*userNameToUserProfile - хранилище пользователей, в качестве ключа используется email*/

    @Nullable
    public UserProfile register(@NotNull String email, @NotNull String login, @NotNull String password) {
        UserProfile userProfile = new UserProfile(email, login, password);
        if(!isOccupied(email)) {
            userProfile.setId();
            userNameToUserProfile.put(email, userProfile);
            return userProfile;
        }
        return null;
    }


    public boolean login(@NotNull String email, @NotNull String password) {
        if (isOccupied(email)) {
            return userNameToUserProfile.get(email).getPassword().equals(password);
        }
        return false;
    }

    public UserProfile getUser(@NotNull String email) {
        return userNameToUserProfile.get(email);
    }

    @Nullable
    public UserProfile update(@NotNull UserProfile userProfile, @NotNull UserProfile changedProfile) {
        if(!isEmptyField(changedProfile.getEmail())) {
            if (!changedProfile.getEmail().equals(userProfile.getEmail()) && isOccupied(changedProfile.getEmail())) {
                return null;
            }
        }
        userNameToUserProfile.remove(userProfile.getEmail());
        updateNotNullFields(userProfile, changedProfile);
        userNameToUserProfile.put(userProfile.getEmail(), userProfile);
        return userProfile;
    }

    private boolean isOccupied(String key) {
        return userNameToUserProfile.containsKey(key);
    }

    private boolean isEmptyField(String field) {
        return (field == null) || field.isEmpty();
    }

    private void updateNotNullFields(@NotNull UserProfile userProfile, @NotNull UserProfile changedProfile) {
        if(!isEmptyField(changedProfile.getEmail())) {
            userProfile.setEmail(changedProfile.getEmail());
        }
        if(!isEmptyField(changedProfile.getLogin())) {
            userProfile.setLogin(changedProfile.getLogin());
        }
        if(!isEmptyField(changedProfile.getPassword())) {
            userProfile.setPassword(changedProfile.getPassword());
        }
    }
}

