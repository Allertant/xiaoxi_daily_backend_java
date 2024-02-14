package icu.shiyixi.dailybackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.shiyixi.dailybackend.bean.Plan;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.PlanObjectDto;

import java.util.List;

public interface PlanService extends IService<Plan> {
    /**
     * 根据用户id获取该用户下的所有计划信息
     * @param userId 用户id
     * @return 计划列表
     */
    List<Plan> getPlansByUserId(Long userId);

    /**
     * 根据planid获取planObjDto对象
     * @param planId 计划id
     * @return 计划对象
     */
    R<PlanObjectDto> getPlanObjDto(Long planId);

    /**
     * 添加计划
     * @param dto 计划信息
     * @return 字符串提示
     */
    R<String> addPlan(PlanObjectDto dto);
}
