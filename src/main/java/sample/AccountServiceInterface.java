package sample;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by sergey on 26.03.17.
 */
public interface AccountServiceInterface {
    public UserProfile register(@NotNull String email, @NotNull String login, @NotNull String password);
    public boolean login(@NotNull String email, @NotNull String password);
    public UserProfile getUser(@NotNull String email);
    public UserProfile update(@NotNull UserProfile userProfile, @NotNull UserProfile changedProfile);
    public void updateScore(@NotNull UserProfile userProfile);
    public ArrayList<UserProfile> getSortedUsersByScore();
    public void flush();

}
