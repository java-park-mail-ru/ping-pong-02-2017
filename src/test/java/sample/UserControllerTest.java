package sample;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sample.services.account.AccountServiceInterface;


@SpringBootTest
@RunWith(SpringRunner.class)
//@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
public class UserControllerTest {
    @Autowired
    @Qualifier("AccountServiceDB")
    private AccountServiceInterface accountService;

    @Test
    public void testTest() {
        for (int i = 0; i != 10; ++i) {
            System.out.println(i);
        }
    }
}
