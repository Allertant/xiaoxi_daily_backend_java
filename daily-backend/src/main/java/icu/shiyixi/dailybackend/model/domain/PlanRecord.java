package icu.shiyixi.dailybackend.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 
 * @TableName plan_record
 */
@TableName(value ="plan_record")
@Data
public class PlanRecord implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField(fill = FieldFill.INSERT)
    private Long userId;

    /**
     * 计划id
     */
    private Long planId;

    /**
     * 计划项id
     */
    private Long planDetailId;

    /**
     * 记录时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 是否被删除 0-否 1-是
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}