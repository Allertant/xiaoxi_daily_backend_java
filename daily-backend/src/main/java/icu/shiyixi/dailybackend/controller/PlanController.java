package icu.shiyixi.dailybackend.controller;

import icu.shiyixi.dailybackend.bean.Plan;
import icu.shiyixi.dailybackend.common.BaseContext;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.PlanObjectDto;
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
    public R<PlanObjectDto> planObjectDto(@PathVariable("planId") Long planId) {
        return planService.getPlanObjDto(planId);
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody PlanObjectDto dto) {
        Long userId = BaseContext.getCurrentId();
        return planService.addPlan(dto);
    }
}
