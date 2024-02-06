package icu.shiyixi.dailybackend.bean;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class UpdateUser {
    private Long id;
    private String username;
}
