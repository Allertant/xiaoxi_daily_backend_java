package icu.shiyixi.dailybackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.shiyixi.dailybackend.model.domain.PlanRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PlanRecordMapper extends BaseMapper<PlanRecord> {
    @Select("select * from plan_record where plan_id = #{planId} and DATE_FORMAT(create_time, '%Y-%m-%d') = #{date} and is_deleted=0")
    List<PlanRecord> getRecordByPlanIdAndDate(Long planId, String date);
}
