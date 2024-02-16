package icu.shiyixi.dailybackend.controller;

import icu.shiyixi.dailybackend.bean.Plan;
import icu.shiyixi.dailybackend.bean.PlanRecord;
import icu.shiyixi.dailybackend.common.BaseContext;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.PlanDetailsDto;
import icu.shiyixi.dailybackend.dto.PlanObjectDto;
import icu.shiyixi.dailybackend.dto.PlanRecordDto;
import icu.shiyixi.dailybackend.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    private PlanService planService;

    @GetMapping("/list")
    public R<List<Plan>> getPlanList() {
        Long userId = BaseContext.getCurrentId();
        List<Plan> plans = planService.getPlansByUserId(userId);
        return R.success(plans);
    }

    @GetMapping("/{planId}")
    public R<PlanObjectDto> getPlanObjectDto(@PathVariable("planId") Long planId) {
        return planService.getPlanObjDto(planId);
    }

    @PostMapping("/add")
    public R<String> addPlanObjectDto(@RequestBody PlanObjectDto dto) {
        return planService.addPlan(dto);
    }

    @PostMapping("/update")
    public R<String> updatePlanObjectDto(@RequestBody PlanObjectDto dto) {
        return planService.updatePlan(dto);
    }

    @PostMapping("/seton/{planId}")
    public R<String> setOnPlanByPlanId(@PathVariable("planId") Long planId) {
        return planService.setOnPlanByPlanId(planId);
    }

    @PostMapping("/details")
    public R<List<PlanDetailsDto>> getDetailsForShow() {
        return planService.getDetailsForShow();
    }

    @GetMapping("/begin")
    public R<String> beginPlan(Long planDetailId) {
        log.info("/plan/begin,planDetailId:{}, ", planDetailId);
        return planService.beginPlan(planDetailId);
    }

    @GetMapping("/history")
    public R<List<PlanRecordDto>> getPlanHistory() {
        return planService.getPlanHistory();
    }
}
