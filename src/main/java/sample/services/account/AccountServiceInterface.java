package sample.services.account;

import org.jetbrains.annotations.NotNull;
import sample.UserProfile;

import java.util.List;

/**
 * Created by sergey on 26.03.17.
 */


public interface AccountServiceInterface {
    UserProfile register(UserProfile userProfile);
    boolean login(@NotNull String email, @NotNull String password);
    UserProfile getUser(@NotNull String email);
    UserProfile update(@NotNull UserProfile userProfile, @NotNull UserProfile changedProfile);
    UserProfile updateScore(@NotNull UserProfile userProfile);
    List<UserProfile> getSortedUsersByScore(int count);
    void flush();
}
