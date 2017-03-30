package sample.services.account;

import sample.UserProfile;

import java.math.BigInteger;
import java.util.Random;

public abstract class UserRelatedTest {
    public static final int RADIX = 32;
    public static final int NUM_BITS = 130;
    private Random random = new Random();

    protected UserProfile getRandomUser() {
        return new UserProfile(getRandomString(), getRandomString(), getRandomString());
    }

    protected String getRandomString() {
        return new BigInteger(NUM_BITS, random).toString(RADIX);
    }

    protected boolean isEqual(UserProfile user1, UserProfile user2) {
        return user1.getEmail().equals(user2.getEmail()) && user1.getLogin().equals(user2.getLogin());
    }
}
