package icu.shiyixi.dailybackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.plan.PlanDetailsDto;
import icu.shiyixi.dailybackend.dto.plan.PlanObjectDto;
import icu.shiyixi.dailybackend.dto.plan.PlanRecordDto;
import icu.shiyixi.dailybackend.model.domain.Plan;

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
    R<PlanObjectDto> getPlanObjDtoByPlanId(Long planId);

    /**
     * 添加计划
     * @param dto 计划信息
     * @return 字符串提示
     */
    R<String> addPlan(PlanObjectDto dto);

    /**
     * 修改计划
     * @param dto 待修改的对象
     * @return 字符串提示
     */
    R<String> updatePlan(PlanObjectDto dto);

    /**
     * 根据计划 id 设置使用该计划
     * @param planId 计划id
     * @return 字符串提示
     */
    R<String> setOnPlanByPlanId(Long planId);

    /**
     * 根据计划 id 获取计划的详细信息
     * @return 计划详情 dto
     */
    R<List<PlanDetailsDto>> getDetails();

    /**
     * 开始计划
     * @param planDetailId 计划项id
     * @return 字符串说明
     */
    R<String> beginPlan(Long planDetailId);

    /**
     * 根据计划 id 获取计划历史
     * @return R<List<PlanRecordDto>>
     */
    R<List<PlanRecordDto>> getPlanHistory();

    /**
     * 根据id删除计划
     * @param planId 待删除的计划 id
     * @return 字符串提示
     */
    R<String> removePlan(Long planId);
}
