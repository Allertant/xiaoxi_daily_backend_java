package icu.shiyixi.dailybackend.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlanRecord {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @TableField(fill = FieldFill.INSERT)
    private Long userId;
    private Long planId;
    private Long planDetailId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
