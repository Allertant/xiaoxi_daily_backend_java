package icu.shiyixi.dailybackend.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * 
 * @TableName plan_detail
 */
@TableName(value ="plan_detail")
@Data
public class PlanDetail implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 计划id
     */
    private Long planId;

    /**
     * 开始时间
     */
    private LocalTime beginTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 该计划项在整个计划中的位置
     */
    private Integer orderNum;

    /**
     * 是否被删除 0-否 1-是
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}