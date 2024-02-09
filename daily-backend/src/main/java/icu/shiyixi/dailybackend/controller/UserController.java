package icu.shiyixi.dailybackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import icu.shiyixi.dailybackend.bean.User;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.UserLoginDto;
import icu.shiyixi.dailybackend.dto.UserRegisterDto;
import icu.shiyixi.dailybackend.service.UserService;
import icu.shiyixi.dailybackend.token.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody UserLoginDto user) {
        return userService.login(user);
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody UserRegisterDto user) {
        return userService.register(user);
    }


}
