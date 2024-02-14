package icu.shiyixi.dailybackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.shiyixi.dailybackend.bean.Plan;
import icu.shiyixi.dailybackend.bean.PlanDetail;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.PlanObjectDto;
import icu.shiyixi.dailybackend.mapper.PlanDetailMapper;
import icu.shiyixi.dailybackend.mapper.PlanMapper;
import icu.shiyixi.dailybackend.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.ToDoubleBiFunction;

@Slf4j
@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan> implements PlanService {
    @Autowired
    private PlanMapper planMapper;
    @Autowired
    private PlanDetailMapper planDetailMapper;

    @Override
    public List<Plan> getPlansByUserId(Long userId) {
        LambdaQueryWrapper<Plan> q = new LambdaQueryWrapper<>();
        q.eq(Plan::getUserId, userId);
        return planMapper.selectList(q);
    }

    @Override
    public R<PlanObjectDto> getPlanObjDto(Long planId) {
        // 准备返回的对象
        PlanObjectDto dto = new PlanObjectDto();

        // 获取计划表中的数据
        Plan plan = planMapper.selectById(planId);
        BeanUtils.copyProperties(plan, dto);

        // 获取计划项并插入
        LambdaQueryWrapper<PlanDetail> q = new LambdaQueryWrapper<>();
        q.eq(PlanDetail::getPlanId, planId);
        List<PlanDetail> planDetails = planDetailMapper.selectList(q);

        // 将计划项插入dto中
        dto.setDetails(planDetails);

        return R.success(dto);
    }

    @Override
    @Transactional
    public R<String> addPlan(PlanObjectDto dto) {
        // 验证数据合法性
        if(dto.getName() == null) {
            return R.error("计划名为空");
        }

        // 准备要插入的对象
        Plan plan = new Plan();

        // 准备数据
        List<PlanDetail> details = dto.getDetails();
        BeanUtils.copyProperties(dto, plan);
        plan.setId(null);
        plan.setCreateTime(null);
        plan.setUpdateTime(null);

        // 插入对象
        planMapper.insert(plan);
        Long planId = plan.getId();
        for (PlanDetail detail : details) {
            detail.setPlanId(planId);
            detail.setId(null);
            planDetailMapper.insert(detail);
        }

        return R.success("计划添加成功");
    }
}
