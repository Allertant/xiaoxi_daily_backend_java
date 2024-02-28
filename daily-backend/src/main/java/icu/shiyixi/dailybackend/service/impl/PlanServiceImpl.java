package icu.shiyixi.dailybackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.shiyixi.dailybackend.cache.PlanDetailDtosCache;
import icu.shiyixi.dailybackend.cache.PlanHistoryCache;
import icu.shiyixi.dailybackend.cache.PlanObjectDtoCache;
import icu.shiyixi.dailybackend.cache.PlansCache;
import icu.shiyixi.dailybackend.common.BaseContext;
import icu.shiyixi.dailybackend.common.ErrorCode;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.plan.PlanDetailsDto;
import icu.shiyixi.dailybackend.dto.plan.PlanObjectDto;
import icu.shiyixi.dailybackend.dto.plan.PlanRecordDto;
import icu.shiyixi.dailybackend.exception.BusinessException;
import icu.shiyixi.dailybackend.mapper.PlanDetailMapper;
import icu.shiyixi.dailybackend.mapper.PlanMapper;
import icu.shiyixi.dailybackend.mapper.PlanRecordMapper;
import icu.shiyixi.dailybackend.model.domain.Plan;
import icu.shiyixi.dailybackend.model.domain.PlanDetail;
import icu.shiyixi.dailybackend.model.domain.PlanRecord;
import icu.shiyixi.dailybackend.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.swing.plaf.metal.MetalBorders;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.alibaba.fastjson.JSON.parse;

@Slf4j
@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan> implements PlanService {
    @Resource
    private PlanMapper planMapper;
    @Resource
    private PlanDetailMapper planDetailMapper;
    @Resource
    private PlanRecordMapper planRecordMapper;
    @Resource
    private PlanObjectDtoCache planObjectDtoCache;
    @Resource
    private PlansCache plansCache;
    @Resource
    private PlanDetailDtosCache planDetailDtosCache;
    @Resource
    private PlanHistoryCache planHistoryCache;

    @Override
    public R<PlanObjectDto> getPlanObjDtoByPlanId(Long planId) {
        // 获取缓存
        PlanObjectDto cachePlanObjectDto = planObjectDtoCache.getCachePlanObjectDto(planId);
        if(cachePlanObjectDto != null) {
            return R.success(cachePlanObjectDto);
        }
        // 准备返回的对象
        PlanObjectDto dto = new PlanObjectDto();

        // 获取计划表中的数据
        Plan plan = planMapper.selectById(planId);
        BeanUtils.copyProperties(plan, dto);

        // 获取计划项
        LambdaQueryWrapper<PlanDetail> q = new LambdaQueryWrapper<>();
        q.eq(PlanDetail::getPlanId, planId);
        List<PlanDetail> planDetails = planDetailMapper.selectList(q);

        // 将计划项插入dto中
        dto.setDetails(planDetails);

        //写入缓存
        planObjectDtoCache.setCachePlanObjectDto(dto);

        return R.success(dto);
    }

    @Override
    public List<Plan> getPlansByUserId(Long userId) {
        // 尝试获取缓存
        List<Plan> cachePlans = plansCache.getCachePlans(userId);
        if(cachePlans != null) {
            return cachePlans;
        }

        // 数据库查询
        LambdaQueryWrapper<Plan> q = new LambdaQueryWrapper<>();
        q.eq(Plan::getUserId, userId);
        List<Plan> plans = planMapper.selectList(q);

        // 设置缓存
        plansCache.setCachePlans(plans, userId);
        return plans;
    }

    @Override
    @Transactional
    public R<String> addPlan(PlanObjectDto dto) {
        // 验证数据合法性
        if(dto.getName() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划名不能为空");
        }
        // 校验计划对象中的用户是否为该用户
        Long userId = BaseContext.getCurrentId();
        if (!Objects.equals(userId, dto.getUserId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法操作，只能添加自己的计划");
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

        // 清除缓存
        plansCache.removeCachePlans(userId);
        return R.success("计划添加成功");
    }

    @Override
    public R<String> removePlan(Long planId) {
        Long userId = BaseContext.getCurrentId();
        // 检查该计划是否属于该用户
        LambdaQueryWrapper<Plan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Plan::getUserId, userId);
        wrapper.eq(Plan::getId,planId);
        int count = this.count(wrapper);
        if(count == 0) {
            throw new BusinessException(ErrorCode.PLAN_NULL);
        }

        // 删除计划
        boolean b = this.removeById(planId);
        if(b) {
            // 清除缓存
            planObjectDtoCache.removeCachePlanObjectDto(planId);
            plansCache.removeCachePlans(userId);
            return R.success("删除成功", "删除计划成功");
        }else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常");
        }
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划不存在");
        }

        // 1. 更新计划
        BeanUtils.copyProperties(dto, plan);
        planMapper.updateById(plan);

        // 2. 更新计划项
        // 2.1 删除之前存在的计划项目
        planDetailMapper.delete(new LambdaQueryWrapper<PlanDetail>()
                .eq(PlanDetail::getPlanId, plan.getId()));
        List<PlanDetail> details = dto.getDetails();
        // 2.2 插入计划项目
        details.forEach(item -> {
            // 2.2 依次插入计划项
            item.setPlanId(planId);
            planDetailMapper.insert(item);
        });
        log.info("计划id：{},更新成功", plan.getId());

        // 清除缓存
        planObjectDtoCache.removeCachePlanObjectDto(planId);

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
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划不存在");
        }

        // 2. 清除该用户所有计划的使用权限
        List<Plan> plans = planMapper.selectList(new LambdaQueryWrapper<Plan>()
                .eq(Plan::getIsOn, 1)
                .eq(Plan::getUserId, userId));
        plans.forEach(item -> {
            item.setIsOn(0);
            planMapper.updateById(item);
        });

        // 3. 设置该用户使用该计划
        plan.setIsOn(1);
        planMapper.updateById(plan);

        // 清除缓存
        plansCache.removeCachePlans(userId);

        return R.success("设置成功", "设置成功");
    }

    @Override
    public R<List<PlanDetailsDto>> getDetails() {
        Long userId = BaseContext.getCurrentId();

        Plan plan = getOne(new LambdaQueryWrapper<Plan>()
                .eq(Plan::getUserId, userId)
                .eq(Plan::getIsOn, 1));

        if(plan == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前还没有正在使用的计划");
        }

        Long planId = plan.getId();

        // 获取缓存
        List<PlanDetailsDto> cachePlanDetailDtos = planDetailDtosCache.getCachePlanDetailDtos(planId);
        if(cachePlanDetailDtos != null) {
            return R.success(cachePlanDetailDtos);
        }


        // 获取原始计划项数组
        List<PlanDetail> planDetails = planDetailMapper.selectList(new LambdaQueryWrapper<PlanDetail>()
                .eq(PlanDetail::getPlanId, planId));

        // 转换成计划项 dto 数组
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
            }

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

        // 添加缓存
        planDetailDtosCache.setCachePlanDetailDtos(planDetailsDtos, planId);

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
                .eq(Plan::getUserId,userId)
                .eq(Plan::getUserId, userId));
        if(plan == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前没有可用的计划");
        }
        // 1.2 打卡
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "计划项不存在");
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该项目已经打卡过了");
        }

        // 3. 打卡
        log.info("planDetailId:{}, 检查无误，正式开始打卡", planDetailId);
        PlanRecord planRecord = new PlanRecord();
        planRecord.setPlanId(planId);
        planRecord.setPlanDetailId(planDetailId);
        int insert = planRecordMapper.insert(planRecord);
        if(insert != 0) {
            // 清除缓存
            planDetailDtosCache.removeCachePlanDetailDtos(planId);
            planHistoryCache.removeCachePlanHistory(planId);
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前没有可用的计划");
        }

        Long planId = plan.getId();

        // 获取缓存
        List<PlanRecordDto> cachePlanHistory = planHistoryCache.getCachePlanHistory(planId);
        if (cachePlanHistory != null) {
            return R.success(cachePlanHistory);
        }

        // 获取某个计划的所有打卡记录
        List<PlanRecord> planRecords = planRecordMapper.selectList(new LambdaQueryWrapper<PlanRecord>()
                .eq(PlanRecord::getPlanId, planId));

        // 转移对象
        ArrayList<PlanRecordDto> planRecordDtos = new ArrayList<>();
        planRecords.forEach(item -> {
            PlanRecordDto planRecordDto = new PlanRecordDto();
            BeanUtils.copyProperties(item, planRecordDto);
            planRecordDtos.add(planRecordDto);
        });

        // 获取正在使用的计划项列表
        List<PlanDetail> planDetails = planDetailMapper.selectList(new LambdaQueryWrapper<PlanDetail>()
                .eq(PlanDetail::getPlanId, planId));

        // 给打卡记录添加开始时间、结束时间字段
        planRecordDtos.forEach(planRecordDto -> {
            Long planDetailId = planRecordDto.getPlanDetailId();

            planDetails.forEach(planDetail -> {
                if(Objects.equals(planDetailId, planDetail.getId())) {
                    // 找到对应的计划项
                    planRecordDto.setBeginTime(planDetail.getBeginTime());
                    planRecordDto.setEndTime(planDetail.getEndTime());
                    planRecordDto.setOrderNum(planDetail.getOrderNum());
                }
            });
        });

        // 放入缓存
        planHistoryCache.setCachePlanHistory(planRecordDtos, planId);

        return R.success(planRecordDtos);
    }


}
