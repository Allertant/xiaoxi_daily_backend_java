package icu.shiyixi.dailybackend.dto;

import icu.shiyixi.dailybackend.bean.User;
import lombok.Data;

@Data
public class UserLoginDto extends User {
    private String vcode;
    private Boolean isPhone;
}
