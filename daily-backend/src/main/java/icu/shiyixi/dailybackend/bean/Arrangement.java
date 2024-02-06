package icu.shiyixi.dailybackend.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalTime;

@Data
public class Arrangement {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalTime beginTime;
    private LocalTime endTime;
    @TableField(exist = false)
    private String msg;
    @TableField(exist = false)
    private int code;
}
