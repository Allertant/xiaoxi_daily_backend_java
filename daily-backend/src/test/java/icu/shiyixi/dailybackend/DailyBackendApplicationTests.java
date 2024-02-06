package icu.shiyixi.dailybackend;

import icu.shiyixi.dailybackend.token.TokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class DailyBackendApplicationTests {

    @Test
    void contextLoads() {
    }
    @Test
    void testMills() throws InterruptedException {
        String entoken = TokenUtils.entoken(1L);
        System.out.println(entoken);
        if(TokenUtils.verify(entoken)) {
            Long detoken = TokenUtils.detoken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2OTg0NzcwMTN9.VxpeHm1pBQt8ioUmDx439GukuSOW4hUpCwRHGVTD2NI");
            System.out.println(detoken);
        }
    }

}
