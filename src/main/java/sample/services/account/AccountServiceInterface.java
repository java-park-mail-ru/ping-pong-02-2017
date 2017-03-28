package sample.services.account;

import org.jetbrains.annotations.NotNull;
import sample.UserProfile;

import java.util.ArrayList;

/**
 * Created by sergey on 26.03.17.
 */


public interface AccountServiceInterface {
    public UserProfile register(UserProfile userProfile);
    public boolean login(@NotNull String email, @NotNull String password);
    public UserProfile getUser(@NotNull String email);
    public UserProfile update(@NotNull UserProfile userProfile, @NotNull UserProfile changedProfile);
    public void updateScore(@NotNull UserProfile userProfile);
    public ArrayList<UserProfile> getSortedUsersByScore(int count);
    public void flush();

}
