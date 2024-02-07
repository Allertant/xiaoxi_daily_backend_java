package icu.shiyixi.dailybackend.dto;

import icu.shiyixi.dailybackend.bean.User;
import lombok.Data;

@Data
public class UserDto extends User {
    private String vcode;
}
