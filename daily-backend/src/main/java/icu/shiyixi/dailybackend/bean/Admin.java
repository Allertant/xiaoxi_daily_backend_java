package icu.shiyixi.dailybackend.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Admin {
    private Long id;
    private String username;
    private String password;
    @TableField(exist = false)
    private String vcode;
    private String phone;
    @TableField(exist = false)
    private String code;
}
