package icu.shiyixi.dailybackend.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Plan implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 计划id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    /**
     * 计划名称
     */
    private String name;
    /**
     * 所属用户
     */
    @TableField(fill = FieldFill.INSERT)
    private Long userId;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
    /**
     * 是否正在使用
     */
    private int isOn;
}
