package icu.shiyixi.dailybackend.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;

@Data
public class PlanDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 计划项的id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    /**
     * 计划项所属的计划id
     */
    private Long planId;
    /**
     * 计划项的开始时间
     */
    private LocalTime beginTime;
    /**
     * 计划项的结束时间
     */
    private LocalTime endTime;
    /**
     * 该计划项的顺序
     */
    private int orderNum;
}
