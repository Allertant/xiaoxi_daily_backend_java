package icu.shiyixi.dailybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@ServletComponentScan
@EnableCaching
public class DailyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailyBackendApplication.class, args);
    }

}
