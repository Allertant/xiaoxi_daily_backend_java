package icu.shiyixi.dailybackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.user.*;
import icu.shiyixi.dailybackend.model.domain.User;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userRegisterDto 用户注册信息
     * @return 注册后的用户信息或错误信息
     */
    public R<UserRegisterResDto> register(UserRegisterReqDto userRegisterDto, HttpServletResponse response);

    /**
     * 用户登录
     * @param userRegisterDto 用户登录信息
     * @return 登录后的用户信息或错误信息
     */
    public R<UserLoginResDto> login(UserLoginReqDto userLoginReqDto, HttpServletResponse response);

    /**
     * 更新用户信息
     * @param userUpdateDto 用户更新信息
     * @return 字符串提示
     */
    R<String> updateUserInfo(UserUpdateDto userUpdateDto);
}
