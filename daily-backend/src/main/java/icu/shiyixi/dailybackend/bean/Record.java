package icu.shiyixi.dailybackend.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class Record implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @TableField(fill = FieldFill.INSERT)
    private Long userId;
    private Long arrangement;

    @TableField(exist = false)
    private LocalTime beginTime;
    @TableField(exist = false)
    private LocalTime endTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
