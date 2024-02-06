package icu.shiyixi.dailybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class DailyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyBackendApplication.class, args);
    }

}
