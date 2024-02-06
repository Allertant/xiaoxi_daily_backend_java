package icu.shiyixi.dailybackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.shiyixi.dailybackend.bean.Arrangement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArrangementMapper extends BaseMapper<Arrangement> {
    @Select("select * from arrangement where user_id = #{userId}")
    List<Arrangement> getByUserId(Long userId) ;
}
