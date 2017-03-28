package sample;


import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sample.services.account.AccountServiceDB;
import sample.services.account.AccountServiceInterface;

import static org.junit.Assert.*;


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
        System.out.println(accountService.getSortedUsersByScore());
    }
}
