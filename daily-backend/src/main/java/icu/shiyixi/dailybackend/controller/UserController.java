package icu.shiyixi.dailybackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import icu.shiyixi.dailybackend.bean.User;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.UserDto;
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
    public Map<String, Object> login(@RequestBody UserDto user) {
        // 准备返回的数据
        HashMap<String, Object> map = new HashMap<>();

        // 用户信息
        String password = user.getPassword();
        String username = user.getUsername();

        // 检查验证码是否存在
        String vcode = user.getVcode();
        if(vcode == null) {
            log.info("用户：{}正在登录，验证码为空", username);
            map.put("code", 0);
            map.put("msg", "验证码不能为空");
            return map;
        }

        // 查询验证码是否存在
        String s = stringRedisTemplate.opsForValue().get(vcode);
        if(s == null) {
            log.info("用户：{}正在登录，验证码为{}，但是查询cache不存在", username, vcode);
            map.put("code", 0);
            map.put("msg", "验证码错误");
            return map;
        }

        // 取出密码并进行 MD5 加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("passwrod: {}", password);
        user.setPassword(password);

        // 查询数据库看用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        User checkedUser = userService.getOne(queryWrapper);

        if(checkedUser == null) {
            log.info("用户：{}正在登录，但用户不存在",username);
            map.put("code", 0);
            map.put("msg", "用户不存在");
        } else if(!Objects.equals(checkedUser.getPassword(), password)) {
            log.info("用户：{}正在登录，但用密码错误",username);
            map.put("code", 0);
            map.put("msg", "密码错误");
        } else {
            log.info("用户：{}正在登录，登录成功",username);
            map.put("code", 1);
            map.put("msg", "登录成功");
            map.put("userId", checkedUser.getId().toString());
            map.put("userName", checkedUser.getUsername());
            // 保存jwt字符串
            map.put("jwt", TokenUtils.entoken(checkedUser.getId()));
        }
        return map;
    }


}
