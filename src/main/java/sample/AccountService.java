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
    public UserProfile update(@NotNull String email, @NotNull UserProfile changedProfile) {
        if(isOccupied(email) && (changedProfile.getEmail().equals(email) || !isOccupied(changedProfile.getEmail()))) {
            UserProfile userProfile = new UserProfile(userNameToUserProfile.get(email));
            userNameToUserProfile.remove(email);
            //if(changedProfile.login != null || changedProfile.email != null || changedProfile.password != null) {
            userProfile = getNewProfile(userProfile, changedProfile);
            userNameToUserProfile.put(userProfile.getEmail(),userProfile);
            return userProfile;
        }

        return null;
    }

    private boolean isOccupied(String key) {
        return userNameToUserProfile.containsKey(key);
    }

    private UserProfile getNewProfile(@NotNull UserProfile userProfile, @NotNull UserProfile changedProfile) {
        if(changedProfile.getLogin() != null) {
            userProfile.setLogin(changedProfile.getLogin());
        }
        if(changedProfile.getEmail() != null) {
            userProfile.setEmail(changedProfile.getEmail());
        }
        if(changedProfile.getPassword() != null) {
            userProfile.setPassword(changedProfile.getPassword());
        }
        return userProfile;
    }
}

