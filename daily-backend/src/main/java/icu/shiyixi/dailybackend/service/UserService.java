package icu.shiyixi.dailybackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.shiyixi.dailybackend.bean.User;
import icu.shiyixi.dailybackend.dto.UserLoginDto;
import icu.shiyixi.dailybackend.dto.UserRegisterDto;

import java.util.Map;

public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userRegisterDto 用户注册信息
     * @return 注册后的用户信息或错误信息
     */
    public Map<String, Object> register(UserRegisterDto userRegisterDto);

    /**
     * 用户登录
     * @param userRegisterDto 用户登录信息
     * @return 登录后的用户信息或错误信息
     */
    public Map<String, Object> login(UserLoginDto userRegisterDto);
}
