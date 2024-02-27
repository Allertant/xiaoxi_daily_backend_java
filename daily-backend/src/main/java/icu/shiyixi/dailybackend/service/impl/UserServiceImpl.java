package icu.shiyixi.dailybackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.shiyixi.dailybackend.common.BaseContext;
import icu.shiyixi.dailybackend.common.ErrorCode;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.user.*;
import icu.shiyixi.dailybackend.exception.BusinessException;
import icu.shiyixi.dailybackend.mapper.UserMapper;
import icu.shiyixi.dailybackend.model.domain.User;
import icu.shiyixi.dailybackend.service.UserService;
import icu.shiyixi.dailybackend.token.TokenUtils;
import icu.shiyixi.dailybackend.utils.CookieSetterUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 验证数据是否为空
     * @param map 要校验的数据 map
     */
    private void checkBlank(Map<String, String> map) {
        map.forEach((key, value) -> {
            if (StringUtils.isAnyBlank(value)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, key+"不能为空");
            }
        });
    }
    private void checkBlankNotAll(Map<String, String> map) {
        AtomicBoolean isEmpty = new AtomicBoolean(true);
        map.forEach((key, value) -> {
            if(!StringUtils.isAnyBlank(value)) {
                // 不为空
                isEmpty.set(false);
            }
        });
        if (isEmpty.get()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
    }

    /**
     * 检查验证码是否存在
     * @param vcode 验证码
     */
    private void checkVcode(String vcode) {
        String s = stringRedisTemplate.opsForValue().get(vcode);
        if(s == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
    }





    /**
     * 注册功能
     * @param userRegisterDto 用户注册信息
     * @return
     */
    @Override
    @Transactional
    public R<UserRegisterResDto> register(UserRegisterReqDto userRegisterDto, HttpServletResponse response) {
        // 准备插入的对象
        User user = null;

        // 用户信息
        String password = userRegisterDto.getPassword();
        String vcode = userRegisterDto.getVcode();
        String phone = userRegisterDto.getPhone();

        // 校验数据是否为空
        Map<String, String> checkMap = new HashMap<>();
        checkMap.put("手机号", phone);
        checkMap.put("密码", password);
        checkMap.put("验证码", vcode);
        checkBlank(checkMap);

        // 查询验证码是否正确
        checkVcode(vcode);

        // 判断手机号是否已经被注册了
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        user = getOne(queryWrapper);
        if(user != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号已经被注册了");
        }

        // 创建user对象
        user = new User();

        // 密码加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 设置密码
        user.setPassword(password);
        // 设置用户名
        user.setUsername("user_" + UUID.randomUUID());
        // 设置手机号
        user.setPhone(phone);

        // 插入数据库
        boolean ans = save(user);
        // 注册用户成功
        if(ans) {
            // 准备数据
            String username = user.getUsername();
            Long userId = user.getId();

            UserRegisterResDto userRegisterResDto = new UserRegisterResDto();
            userRegisterResDto.setUserId(userId);
            userRegisterResDto.setUsername(username);

            // 设置 cookie
            CookieSetterUtils.setCookie(response, userId);

            return R.success(userRegisterResDto);
        }else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册失败");
        }
    }

    @Override
    public R<UserLoginResDto> login(UserLoginReqDto userLoginDto, HttpServletResponse response) {
        // 用户信息
        String password = userLoginDto.getPassword();
        String username = userLoginDto.getUsername();
        String vcode = userLoginDto.getVcode();
        String phone = userLoginDto.getPhone();
        Boolean isPhone = userLoginDto.getIsPhone();

        // 校验数据是否为空
        Map<String, String> checkMap = new HashMap<>();
        checkMap.put("密码", password);
        checkMap.put("验证码", vcode);
        checkMap.put("是否为手机号", isPhone.toString());
        checkBlank(checkMap);

        // 校验用户名和手机号是否都为空
        checkMap.clear();
        checkMap.put("手机号", phone);
        checkMap.put("用户名", username);
        checkBlankNotAll(checkMap);

        // 查询验证码是否正确
        checkVcode(vcode);

        // 取出密码并进行 MD5 加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        userLoginDto.setPassword(password);

        // 查询数据库看用户是否存在
        User checkedUser;
        // 如果是用户名
        if(isPhone.equals(Boolean.TRUE)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            checkedUser = getOne(queryWrapper);
        }else {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, username);
            checkedUser = getOne(queryWrapper);
        }

        if(checkedUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        } else if(!Objects.equals(checkedUser.getPassword(), password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        } else {
            Long userId = checkedUser.getId();
            username = checkedUser.getUsername();

            UserLoginResDto userLoginResDto = new UserLoginResDto();
            userLoginResDto.setUsername(username);
            userLoginResDto.setUserId(userId);

            // 设置cookie
            CookieSetterUtils.setCookie(response, userId);
            return R.success(userLoginResDto, "登录成功");
        }
    }

    @Override
    public R<String> updateUserInfo(UserUpdateDto userUpdateDto) {
        // 修改数据
        Long userId = BaseContext.getCurrentId();
        User user = this.getById(userId);
        if(user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        BeanUtils.copyProperties(userUpdateDto, user);
        boolean b = this.updateById(user);
        if(b) {
            return R.success("更新成功", "更新成功");
        }else {
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR, "系统错误");
        }
    }


}
