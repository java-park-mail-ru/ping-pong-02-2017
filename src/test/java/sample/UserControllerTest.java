package sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import sample.services.account.AccountServiceInterface;
import sample.services.account.UserRelatedTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
public class UserControllerTest extends UserRelatedTest {

    @Autowired
    @Qualifier("AccountServiceDB")
    private AccountServiceInterface accountService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    private UserProfile user;

    @Before
    public void setup() {
        final Flyway flyway = new Flyway();
        flyway.setDataSource("jdbc:h2:mem:testdb", "sa", "");
        flyway.migrate();

        user = getRandomUser();
        accountService.register(user);
    }

    @Test
    public void testNoEmailRegistration() throws Exception {
        final UserProfile userProfile = getRandomUser();
        userProfile.setEmail(null);
        mockMvc
                .perform(post("/api/user/registration")
                        .content(mapper.writeValueAsString(userProfile))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testNoPasswordRegistration() throws Exception {
        final UserProfile userProfile = getRandomUser();
        userProfile.setPassword(null);
        mockMvc
                .perform(post("/api/user/registration")
                        .content(mapper.writeValueAsString(userProfile))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testNoLoginRegistration() throws Exception {
        final UserProfile userProfile = getRandomUser();
        userProfile.setLogin(null);
        mockMvc
                .perform(post("/api/user/registration")
                        .content(mapper.writeValueAsString(userProfile))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testValidRegistration() throws Exception {
        final UserProfile userProfile = getRandomUser();
        mockMvc
                .perform(post("/api/user/registration")
                        .content(mapper.writeValueAsString(userProfile))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testRepetitiveRegistration() throws Exception {
        mockMvc
                .perform(post("/api/user/registration")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn();
    }
}
