package sample.services.account;

import org.jetbrains.annotations.NotNull;
import sample.UserProfile;

import java.util.ArrayList;

/**
 * Created by sergey on 26.03.17.
 */
public interface AccountServiceInterface {
    UserProfile register(@NotNull String email, @NotNull String login, @NotNull String password);
    boolean login(@NotNull String email, @NotNull String password);
    UserProfile getUser(@NotNull String email);
    UserProfile update(@NotNull UserProfile userProfile, @NotNull UserProfile changedProfile);
    void updateScore(@NotNull UserProfile userProfile);
    ArrayList<UserProfile> getSortedUsersByScore();
    void flush();

}
