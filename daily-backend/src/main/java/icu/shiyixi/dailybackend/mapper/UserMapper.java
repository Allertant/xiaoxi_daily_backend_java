package icu.shiyixi.dailybackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.shiyixi.dailybackend.model.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
