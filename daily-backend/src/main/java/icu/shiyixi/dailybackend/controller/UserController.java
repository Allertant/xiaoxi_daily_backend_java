package icu.shiyixi.dailybackend.controller;

import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.user.*;
import icu.shiyixi.dailybackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户登录
     * @param userLoginReqDto 用户登录信息
     * @param response 响应体
     * @return R<UserLoginResDto>
     */
    @PostMapping("/login")
    public R<UserLoginResDto> login(@RequestBody UserLoginReqDto userLoginReqDto, HttpServletResponse response) {
        return userService.login(userLoginReqDto, response);
    }

    /**
     * 用户注册
     * @param userRegisterReqDto 用户注册信息
     * @param response 响应体
     * @return R<UserRegisterResDto>
     */
    @PostMapping("/register")
    public R<UserRegisterResDto> register(@RequestBody UserRegisterReqDto userRegisterReqDto, HttpServletResponse response) {
        return userService.register(userRegisterReqDto, response);
    }

    @PostMapping("/update")
    public R<String> update(@RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUserInfo(userUpdateDto);
    }


}
