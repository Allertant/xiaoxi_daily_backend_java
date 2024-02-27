package icu.shiyixi.dailybackend.controller;

import icu.shiyixi.dailybackend.common.BaseContext;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.plan.PlanDetailsDto;
import icu.shiyixi.dailybackend.dto.plan.PlanObjectDto;
import icu.shiyixi.dailybackend.dto.plan.PlanRecordDto;
import icu.shiyixi.dailybackend.model.domain.Plan;
import icu.shiyixi.dailybackend.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/plan")
public class PlanController {

    @Resource
    private PlanService planService;

    /**
     * 获取用户列表信息
     * @return 用户列表信息
     */
    @GetMapping("/list")
    public R<List<Plan>> getPlanList() {
        Long userId = BaseContext.getCurrentId();
        List<Plan> plans = planService.getPlansByUserId(userId);
        return R.success(plans);
    }

    /**
     * 根据用户id获取计划信息
     * @param planId 计划 id
     * @return 计划对象dto
     */
    @GetMapping("/{planId}")
    public R<PlanObjectDto> getPlanObjectDto(@PathVariable("planId") Long planId) {
        return planService.getPlanObjDtoByPlanId(planId);
    }

    /**
     * 添加计划信息
     * @param dto 计划对象dto
     * @return 字符串提示
     */
    @PostMapping("/add")
    public R<String> addPlanObjectDto(@RequestBody PlanObjectDto dto) {
        return planService.addPlan(dto);
    }

    /**
     * 更新计划信息
     * @param dto 计划对象信息
     * @return 字符串提示
     */
    @PostMapping("/update")
    public R<String> updatePlanObjectDto(@RequestBody PlanObjectDto dto) {
        return planService.updatePlan(dto);
    }

    /**
     * 删除某个计划
     * @param planId 待删除的计划id
     * @return 字符串提示
     */
    @PostMapping("/remove/{planId}")
    public R<String> deletePlan(@PathVariable("planId") Long planId) {
        return planService.removePlan(planId);
    }

    /**
     * 启用计划
     * @param planId 计划id
     * @return 字符串提示
     */
    @PostMapping("/seton/{planId}")
    public R<String> setOnPlanByPlanId(@PathVariable("planId") Long planId) {
        return planService.setOnPlanByPlanId(planId);
    }

    /**
     * 获取某计划的详情列表
     * @return 某计划详情列表
     */
    @PostMapping("/details")
    public R<List<PlanDetailsDto>> getDetailsIsOn() {
        return planService.getDetails();
    }

    /**
     * 启动某个计划的某个计划项
     * @param planDetailId 计划详情id
     * @return 字符串提示
     */
    @GetMapping("/begin")
    public R<String> beginPlan(Long planDetailId) {
        return planService.beginPlan(planDetailId);
    }

    /**
     * 获取使用中的计划的历史打卡
     * @return 历史打卡的dto数组
     */
    @GetMapping("/history")
    public R<List<PlanRecordDto>> getPlanHistory() {
        return planService.getPlanHistory();
    }


}
