package sample.services.account;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import sample.UserProfile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class AccountServiceTest extends UserRelatedTest {
    @Autowired
    @Qualifier("AccountServiceDB")
    private AccountServiceInterface accountService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Before
    public void initDB() {
        jdbcTemplate.update("DROP TABLE IF EXISTS \"User\" CASCADE");
        jdbcTemplate.update(
                "CREATE TABLE IF NOT EXISTS \"User\" (\n" +
                "  id SERIAL PRIMARY KEY,\n" +
                "  login VARCHAR NOT NULL,\n" +
                "  email VARCHAR UNIQUE NOT NULL,\n" +
                "  password VARCHAR(256) NOT NULL,\n" +
                "  score INTEGER\n" +
                ");");

        System.out.println("Database initialized");
    }

    @Test
    public void testRegisterSuccess() {
        System.out.println("Testing successfull user creation");
        final UserProfile userProfile = getRandomUser();
        final UserProfile savedUser = accountService.register(userProfile);

        assertTrue(isEqual(userProfile, savedUser));

        System.out.println("OK");
    }

    @Test
    public void testRegisterConflict() {
        System.out.println("Testing conflict user creation");
        final UserProfile userProfile = getRandomUser();

        assertNotEquals(accountService.register(userProfile), null);
        assertEquals(accountService.register(userProfile), null);
        System.out.println("OK");
    }

    @Test
    public void testGetUserSuccess() {
        System.out.println("Testing successfull user extraction");
        final UserProfile userProfile = getRandomUser();

        accountService.register(userProfile);
        final UserProfile extractedUser = accountService.getUser(userProfile.getEmail());

        assertTrue(isEqual(userProfile, extractedUser));
        System.out.println("OK");
    }

    @Test
    public void testSuccessfullLogin() {
        System.out.println("Testing successfull credentials validation");
        final UserProfile userProfile = getRandomUser();

        accountService.register(userProfile);
        final boolean isValid = accountService.login(userProfile.getEmail(), userProfile.getPassword());

        assertTrue(isValid);
        System.out.println("OK");
    }

    @Test
    public void testLoginFailed() {
        System.out.println("Testing failed credentials validation");
        final UserProfile userProfile = getRandomUser();
        final UserProfile anotherUser = getRandomUser();

        accountService.register(userProfile);
        final boolean isValid = accountService.login(userProfile.getEmail(), anotherUser.getPassword());

        assertFalse(isValid);
        System.out.println("OK");
    }

    @Test
    public void getUserFail() {
        System.out.println("Testing non-existing user extraction");
        assertEquals(accountService.getUser(getRandomUser().getEmail()), null);
    }

    @Test
    public void testSuccessfullUserUpdate() {
        System.out.println("Testing successfull user update");

        final UserProfile userProfile = getRandomUser();
        final UserProfile userUpdate = getRandomUser();

        userUpdate.setEmail(userProfile.getEmail());

        accountService.register(userProfile);
        final UserProfile newUser = accountService.update(userProfile.getEmail(), userUpdate);

        assertTrue(isEqual(newUser, userUpdate));
        System.out.println("OK");
    }

    @Test
    public void testEmailConflictUserUpdate() {
        System.out.println("Testing email conflict user update");

        final UserProfile firstUser = getRandomUser();
        final UserProfile secondUser = getRandomUser();

        final UserProfile firstUserUpdate = getRandomUser();
        firstUserUpdate.setEmail(secondUser.getEmail());

        accountService.register(firstUser);
        accountService.register(secondUser);

        final UserProfile newUser = accountService.update(firstUser.getEmail(), firstUserUpdate);
        assertEquals(newUser, null);

        System.out.println("OK");
    }

    @Test
    public void testNonExistingUserUpdate() {
        System.out.println("Test non existing user update");

        final UserProfile userProfile = getRandomUser();
        final UserProfile userUpdate = getRandomUser();

        final UserProfile newUser = accountService.update(userProfile.getEmail(), userUpdate);

        assertEquals(newUser, null);
        System.out.println("OK");
    }

    @Test
    public void testSuccessfullScoreUpdate() {
        System.out.println("Test successful score update");

        final UserProfile userProfile = getRandomUser();
        accountService.register(userProfile);

        final int newScore = 100;
        userProfile.setScore(newScore);
        final UserProfile newUser = accountService.updateScore(userProfile);

        assertEquals(newUser.getScore(), newScore);

        System.out.println("OK");
    }

    @Test
    public void testNonExistingUserScoreUpdate() {
        System.out.println("Test non existing user score update");

        final UserProfile extractedUser = accountService.updateScore(getRandomUser());
        assertEquals(extractedUser, null);

        System.out.println("OK");
    }

    @Test
    public void testSuccessfulGetLeaders() {
        System.out.println("Test successful user extraction");

        final int maxRating = 10000;
        final int startRating = 1000;
        final int ratingStep = 100;

        final List<Integer> scoreList = new ArrayList<>();
        for (int i = maxRating; i > startRating; i -= ratingStep) {
            scoreList.add(i);
        }

        for (int score : scoreList) {
            final UserProfile userProfile = getRandomUser();
            userProfile.setScore(score);
            accountService.register(userProfile);
        }

        final List<UserProfile> extractedUsers = accountService.getSortedUsersByScore(scoreList.size());
        final List<Integer> extractedScores = new ArrayList<>();

        for (UserProfile user : extractedUsers) {
            extractedScores.add(user.getScore());
        }

        assertEquals(extractedScores.size(), scoreList.size());
        for (int i = 0; i != extractedScores.size(); ++i) {
            assertEquals(extractedScores.get(i), scoreList.get(i));
        }

        System.out.println("OK");

    }

    @Test
    public void testDataBaseFlush() {

    }
}
