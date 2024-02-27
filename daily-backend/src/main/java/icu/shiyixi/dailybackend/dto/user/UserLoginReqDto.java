package icu.shiyixi.dailybackend.dto.user;

import icu.shiyixi.dailybackend.model.domain.User;
import lombok.Data;

@Data
public class UserLoginReqDto extends User {
    private String vcode;
    private Boolean isPhone;
}
