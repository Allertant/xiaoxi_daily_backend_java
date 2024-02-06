package icu.shiyixi.dailybackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.shiyixi.dailybackend.bean.Record;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {
    @Select("select * from record where user_id = #{userId} and DATE_FORMAT(create_time, '%Y-%m-%d') = #{date}")
    List<Record> getByDateUser(Long userId, String date);
    @Select("select * from record where user_id = #{userId}")
    List<Record> getByUser(Long userId);
}
