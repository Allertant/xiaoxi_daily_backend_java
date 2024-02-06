package icu.shiyixi.dailybackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.shiyixi.dailybackend.bean.User;
import icu.shiyixi.dailybackend.mapper.UserMapper;
import icu.shiyixi.dailybackend.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
