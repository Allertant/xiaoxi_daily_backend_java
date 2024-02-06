package icu.shiyixi.dailybackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import icu.shiyixi.dailybackend.bean.User;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.service.UserService;
import icu.shiyixi.dailybackend.token.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
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
    UserService userService;

    /**
     * 用户登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {

        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("passwrod: {}", password);
        user.setPassword(password);

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        User checkedUser = userService.getOne(queryWrapper);

        HashMap<String, Object> map = new HashMap<>();

        if(checkedUser == null) {
            map.put("code", 0);
            map.put("msg", "用户不存在");
        } else if(!Objects.equals(checkedUser.getPassword(), password)) {
            map.put("code", 0);
            map.put("msg", "密码错误");
        } else {
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
