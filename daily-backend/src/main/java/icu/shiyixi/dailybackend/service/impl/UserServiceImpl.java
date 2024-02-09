package icu.shiyixi.dailybackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.Insert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.shiyixi.dailybackend.bean.User;
import icu.shiyixi.dailybackend.dto.UserLoginDto;
import icu.shiyixi.dailybackend.dto.UserRegisterDto;
import icu.shiyixi.dailybackend.mapper.UserMapper;
import icu.shiyixi.dailybackend.service.UserService;
import icu.shiyixi.dailybackend.token.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public Map<String, Object> register(UserRegisterDto userRegisterDto) {
        // 准备插入的对象
        User user = null;
        // 准备返回的数据
        HashMap<String, Object> map = new HashMap<>();

        // 用户信息
        String password = userRegisterDto.getPassword();
        String vcode = userRegisterDto.getVcode();
        String phone = userRegisterDto.getPhone();

        // 检查手机号是否传入了
        if(phone == null) {
            log.info("手机号为空");
            map.put("code", 0);
            map.put("msg", "手机号不能为空");
            return map;
        }
        // 检查密码是否传入了
        if(password == null) {
            log.info("手机号：{}正在注册，验证码为空", phone);
            map.put("code", 0);
            map.put("msg", "密码不能为空");
            return map;
        }
        // 检查验证码是否传入了
        if(vcode == null) {
            log.info("手机号：{}正在注册，验证码为空", phone);
            map.put("code", 0);
            map.put("msg", "验证码不能为空");
            return map;
        }


        // 查询验证码是否正确
        boolean b = checkVcode(vcode);
        if(!b) {
            log.info("手机号：{}正在注册，验证码为{}，但是查询cache不存在", phone, vcode);
            map.put("code", 0);
            map.put("msg", "验证码错误");
            return map;
        }

        // 判断手机号是否已经被注册了
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        user = getOne(queryWrapper);
        if(user != null) {
            log.info("手机号：{},该用户已经注册过了",phone);
            map.put("code", 0);
            map.put("msg", "该手机号已经注册过了");
            return map;
        }

        // 创建user对象
        user = new User();

        // 取出密码并进行 MD5 加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("passwrod: {}", password);
        // 设置密码
        user.setPassword(password);

        // 设置用户名
        user.setUsername("user_" + UUID.randomUUID());
        // 设置手机号
        user.setPhone(phone);

        // 插入数据库
        boolean ans = save(user);
        if(ans) {
            // 注册用户成功
            String username = user.getUsername();
            Long userId = user.getId();
            log.info("手机号：{}正在注册，注册成功",phone);
            map.put("code", 1);
            map.put("msg", "注册成功");
            map.put("userId", user.getId().toString());
            map.put("userName", username);
            // 保存jwt字符串
            map.put("jwt", TokenUtils.entoken(user.getId()));
        }else {
            log.info("手机号：{}正在注册，注册失败",phone);
            map.put("code", 0);
            map.put("msg", "注册失败");
        }

        // 返回结果
        return map;
    }

    @Override
    public Map<String, Object> login(UserLoginDto userLoginDto) {
        // 准备返回的数据
        HashMap<String, Object> map = new HashMap<>();

        // 用户信息
        String password = userLoginDto.getPassword();
        String username = userLoginDto.getUsername();
        String vcode = userLoginDto.getVcode();

        // 检查验证码是否传入了
        if(vcode == null) {
            log.info("用户：{}正在登录，验证码为空", username);
            map.put("code", 0);
            map.put("msg", "验证码不能为空");
            return map;
        }
        // 检查密码是否传入了
        if(password == null) {
            log.info("用户：{}正在登录，验证码为空", password);
            map.put("code", 0);
            map.put("msg", "密码不能为空");
            return map;
        }
        // 检查用户名是否传入了
        if(username == null) {
            log.info("用户：{}正在登录，用户名为空", username);
            map.put("code", 0);
            map.put("msg", "用户名不能为空");
            return map;
        }

        // 查询验证码是否正确
        boolean b = checkVcode(vcode);
        if(!b) {
            log.info("用户：{}正在登录，验证码为{}，但是查询cache不存在", username, vcode);
            map.put("code", 0);
            map.put("msg", "验证码错误");
            return map;
        }

        // 取出密码并进行 MD5 加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("passwrod: {}", password);
        userLoginDto.setPassword(password);

        // 查询数据库看用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userLoginDto.getUsername());
        User checkedUser = getOne(queryWrapper);

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

    /**
     * 检查验证码是否存在
     * @param vcode
     * @return
     */
    private boolean checkVcode(String vcode) {
        String s = stringRedisTemplate.opsForValue().get(vcode);
        return s != null;
    }

}
