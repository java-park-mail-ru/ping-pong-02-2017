package sample;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by sergey on 23.02.17.
 */

@Service
public class AccountServiceHM implements AccountServiceInterface {
    private Map<String, UserProfile> userStorage = new HashMap<>();

    @Nullable
    @Override
    public UserProfile register(@NotNull String email, @NotNull String login, @NotNull String password) {
        final UserProfile userProfile = new UserProfile(email, login, password);
        if(!userStorage.containsKey(email)) {
            userProfile.setId();
            userStorage.put(email, userProfile);
            return userProfile;
        }
        return null;
    }

    @Override
    public boolean login(@NotNull String email, @NotNull String password) {
        return userStorage.containsKey(email) && passwordEncoder().matches(password, userStorage.get(email).getPassword());
    }

    @Override
    public UserProfile getUser(@NotNull String email) {
        return userStorage.get(email);
    }

    @Nullable
    @Override
    public UserProfile update(@NotNull UserProfile userProfile, @NotNull UserProfile changedProfile) {
        if (!isEmptyField(changedProfile.getEmail())) {
            if (!changedProfile.getEmail().equals(userProfile.getEmail()) &&
                    userStorage.containsKey(changedProfile.getEmail())) {
                return null;
            }
        }

        userStorage.remove(userProfile.getEmail());
        if (passwordEncoder().matches(changedProfile.getPassword(), userProfile.getPassword())) {
            changedProfile.setPassword("");
        }

        updateNotNullFields(userProfile, changedProfile);
        userStorage.put(userProfile.getEmail(), userProfile);
        return userProfile;
    }

    @Override
    public void updateScore(@NotNull UserProfile userProfile) {
        userStorage.put(userProfile.getEmail(), userProfile);
    }

    @Override
    public ArrayList<UserProfile> getSortedUsersByScore() {
        final ArrayList<UserProfile> userProfileArrayList = new ArrayList<UserProfile>();
        userProfileArrayList.addAll(userStorage.values());
        Collections.sort(userProfileArrayList, new Comparator<UserProfile>() {
            @Override
            public int compare(UserProfile o1, UserProfile o2) {
                if(o1.getScore() < o2.getScore()) {
                    return 1;
                }
                else if(o1.getScore() > o2.getScore()) {
                    return -1;
                }
                return 0;
            }
        });

        return  userProfileArrayList;
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

    public void flush() {
        userStorage.clear();
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

