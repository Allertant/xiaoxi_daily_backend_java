package icu.shiyixi.dailybackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.shiyixi.dailybackend.bean.*;
import icu.shiyixi.dailybackend.common.BaseContext;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.PlanDetailsDto;
import icu.shiyixi.dailybackend.dto.PlanObjectDto;
import icu.shiyixi.dailybackend.dto.PlanRecordDto;
import icu.shiyixi.dailybackend.mapper.PlanDetailMapper;
import icu.shiyixi.dailybackend.mapper.PlanMapper;
import icu.shiyixi.dailybackend.mapper.PlanRecordMapper;
import icu.shiyixi.dailybackend.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan> implements PlanService {
    @Autowired
    private PlanMapper planMapper;
    @Autowired
    private PlanDetailMapper planDetailMapper;
    @Autowired
    private PlanRecordMapper planRecordMapper;


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

    @Override
    @Transactional
    public R<String> updatePlan(PlanObjectDto dto) {
        // 查询计划是否存在
        Long planId = dto.getId();
        Plan plan = planMapper.selectById(planId);
        if(plan == null) {
            // 计划不存在
            log.info("计划id：{}，计划不存在", planId);
            return R.error("计划不存在");
        }

        // 1. 更新计划
        BeanUtils.copyProperties(dto, plan);
        planMapper.updateById(plan);

        // 2. 更新计划项
        // 2.1 删除之前存在的计划项目
        planDetailMapper.delete(new LambdaQueryWrapper<PlanDetail>()
                .eq(PlanDetail::getPlanId, plan.getId()));
        List<PlanDetail> details = dto.getDetails();
        details.forEach(item -> {
            // 2.2 依次插入计划项
            planDetailMapper.insert(item);
        });
        log.info("计划id：{},更新成功", plan.getId());
        return R.success("更新成功");
    }

    @Override
    @Transactional
    public R<String> setOnPlanByPlanId(Long planId) {
        Long userId = BaseContext.getCurrentId();

        // 1. 查询该用户是否为该计划的指定人
        Plan plan = planMapper.selectOne(new LambdaQueryWrapper<Plan>()
                .eq(Plan::getId, planId)
                .eq(Plan::getUserId, userId));
        if(plan == null) {
            return R.error("计划不存在");
        }

        // 2. 清除原先用户的使用权限
        List<Plan> plans = planMapper.selectList(new LambdaQueryWrapper<Plan>()
                .eq(Plan::getIsOn, 1));
        plans.forEach(item -> {
            item.setIsOn(0);
            planMapper.updateById(item);
        });

        // 3. 设置该用户使用该计划
        plan.setIsOn(1);
        planMapper.updateById(plan);
        return R.success("设置成功");
    }

    @Override
    public R<List<PlanDetailsDto>> getDetailsForShow() {
        Long userId = BaseContext.getCurrentId();
        // 获取正在被使用的计划
        Plan plan = planMapper.selectOne(new LambdaQueryWrapper<Plan>()
                .eq(Plan::getIsOn, 1)
                .eq(Plan::getUserId, userId));
        if(plan == null) {
            return R.error("当前没有使用中的计划");
        }

        // 获取正在使用的计划项列表
        Long planId = plan.getId();
        List<PlanDetail> planDetails = planDetailMapper.selectList(new LambdaQueryWrapper<PlanDetail>()
                .eq(PlanDetail::getPlanId, planId));
        // 转换
        List<PlanDetailsDto> planDetailsDtos = new ArrayList<>();
        planDetails.forEach(plandetail -> {
            PlanDetailsDto planDetailsDto = new PlanDetailsDto();
            BeanUtils.copyProperties(plandetail, planDetailsDto);
            planDetailsDtos.add(planDetailsDto);
        });

        // 查询打卡记录
        List<PlanRecord> planRecords = planRecordMapper.getRecordByPlanIdAndDate(planId, LocalDate.now().toString());

        // 设置计划项的信息
        planDetailsDtos.forEach(item -> {
            // 计划项id
            Long planDetailId = item.getId();

            // 时间范围
            LocalTime now = LocalTime.now();
            LocalTime beginTime = item.getBeginTime();
            LocalTime endTime = item.getEndTime();

            // 是否已经被打卡过了
            boolean inRecord = false;
            for(PlanRecord planRecord: planRecords) {
                if(Objects.equals(item.getId(), planRecord.getPlanDetailId())) {
                    inRecord = true;
                    break;
                }
            };

            if(now.isBefore(beginTime)) {
                item.setCode(1001);
                item.setMsg("未开始");
            }

            else if(now.isAfter(beginTime) && now.isBefore(endTime) && !inRecord) {
                item.setCode(1002);
                item.setMsg("开始");
            }

            else if(now.isAfter(beginTime) && now.isBefore(endTime) && inRecord) {
                item.setCode(1003);
                item.setMsg("进行中");
            }

            else if(now.isAfter(endTime) && !inRecord) {
                item.setCode(1004);
                item.setMsg("已结束");
            }

            else if(now.isAfter(endTime) && inRecord) {
                item.setCode(1005);
                item.setMsg("已完成");
            }
        });
         return R.success(planDetailsDtos);
    }

    @Override
    @Transactional
    public R<String> beginPlan(Long planDetailId) {
        // 1. 检查该计划项id是否合法
        Long userId = BaseContext.getCurrentId();
        // 1.1 获取正在被使用的计划
        Plan plan = planMapper.selectOne(new LambdaQueryWrapper<Plan>()
                .eq(Plan::getIsOn, 1)
                .eq(Plan::getUserId, userId));
        if(plan == null) {
            return R.error("当前没有可用的计划");
        }
        // 1.2 获取正在使用的计划项列表
        Long planId = plan.getId();
        List<PlanDetail> planDetails = planDetailMapper.selectList(new LambdaQueryWrapper<PlanDetail>()
                .eq(PlanDetail::getPlanId, planId));
        // 1.3 检查
        AtomicBoolean isExisting = new AtomicBoolean(false);
        planDetails.forEach(item -> {
            if(Objects.equals(item.getId(), planDetailId)) {
                isExisting.set(true);
            }
        });
        if(!isExisting.get()) {
            return R.error("计划项id不存在");
        }

        // 2. 判断该计划项是否已经打卡过了
        // 查询打卡记录
        List<PlanRecord> planRecords = planRecordMapper.getRecordByPlanIdAndDate(planId, LocalDate.now().toString());
        AtomicBoolean isBegin = new AtomicBoolean(false);
        planRecords.forEach(item -> {
            if(item.getPlanDetailId().equals(planDetailId)) {
                isBegin.set(true);
            }
        });
        if(isBegin.get()) {
            log.info("planDetailId:{}, 该记录已经打卡过了", planDetailId);
            return R.error("该记录已经打卡过了");
        }

        // 3. 打卡
        log.info("planDetailId:{}, 检查无误，正式开始打卡", planDetailId);
        PlanRecord planRecord = new PlanRecord();
        planRecord.setPlanId(planId);
        planRecord.setPlanDetailId(planDetailId);
        int insert = planRecordMapper.insert(planRecord);
        if(insert != 0) {
            return R.success("打卡成功");
        }else {
            return R.success("打开失败");
        }


    }

    @Override
    public R<List<PlanRecordDto>> getPlanHistory() {
        Long userId = BaseContext.getCurrentId();
        // 获取正在被使用的计划
        Plan plan = planMapper.selectOne(new LambdaQueryWrapper<Plan>()
                .eq(Plan::getIsOn, 1)
                .eq(Plan::getUserId, userId));
        if(plan == null) {
            return R.error("当前没有使用中的计划");
        }

        // 获取某个计划的所有打卡记录
        List<PlanRecord> planRecords = planRecordMapper.selectList(new LambdaQueryWrapper<PlanRecord>()
                .eq(PlanRecord::getPlanId, plan.getId()));
        // 转移对象
        ArrayList<PlanRecordDto> planRecordDtos = new ArrayList<>();
        planRecords.forEach(item -> {
            PlanRecordDto planRecordDto = new PlanRecordDto();
            BeanUtils.copyProperties(item, planRecordDto);
            planRecordDtos.add(planRecordDto);
        });

        // 获取正在使用的计划项列表
        Long planId = plan.getId();
        List<PlanDetail> planDetails = planDetailMapper.selectList(new LambdaQueryWrapper<PlanDetail>()
                .eq(PlanDetail::getPlanId, planId));

        // 给打卡记录添加开始时间、结束时间字段
        planRecordDtos.forEach(planRecordDto -> {
            Long planDetailId = planRecordDto.getPlanDetailId();
            AtomicReference<LocalTime> beginTime = new AtomicReference<>();
            AtomicReference<LocalTime> endTime = new AtomicReference<>();
            AtomicReference<Integer> orderNum = new AtomicReference<>();
            planDetails.forEach(planDetail -> {
                if(Objects.equals(planDetailId, planDetail.getId())) {
                    // 找到对应的计划项
                    beginTime.set(planDetail.getBeginTime());
                    endTime.set(planDetail.getEndTime());
                    orderNum.set(planDetail.getOrderNum());
                }
            });
            planRecordDto.setBeginTime(beginTime.get());
            planRecordDto.setEndTime(endTime.get());
            planRecordDto.setOrderNum(orderNum.get());
        });

        return R.success(planRecordDtos);
    }
}
