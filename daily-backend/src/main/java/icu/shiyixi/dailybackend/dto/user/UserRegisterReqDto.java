package icu.shiyixi.dailybackend.dto.user;

import icu.shiyixi.dailybackend.model.domain.User;
import lombok.Data;

@Data
public class UserRegisterReqDto extends User {
    private String vcode;
}
