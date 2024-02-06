package icu.shiyixi.dailybackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import icu.shiyixi.dailybackend.bean.Admin;
import icu.shiyixi.dailybackend.bean.UpdateUser;
import icu.shiyixi.dailybackend.bean.User;
import icu.shiyixi.dailybackend.common.Constants;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.service.AdminService;
import icu.shiyixi.dailybackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.language.bm.Rule;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/loginByCode")
    public Map<String, Object> loginByCode(@RequestBody Admin admin, HttpSession session) {
        HashMap<String, Object> map = new HashMap<>();

        // 判断用户是否存在
        LambdaQueryWrapper<Admin> w = new LambdaQueryWrapper<>();
        w.eq(Admin::getPhone, admin.getPhone());
        Admin checkAdmin = adminService.getOne(w);
        if(checkAdmin == null) {
            map.put("code", 0);
            map.put("msg", "用户不存在");
            return map;
        }

        admin.setUsername(checkAdmin.getUsername());
        admin.setId(checkAdmin.getId());

        // 判断 code
        String phone = admin.getPhone();
        String code = redisTemplate.opsForValue().get(phone);
        if(ObjectUtils.isEmpty(code)) {
            map.put("code", 0);
            map.put("msg", "验证码过期或未获取验证码");
            return map;
        }

        if(!code.equals(admin.getCode())) {
            map.put("code", 0);
            map.put("msg", "验证码错误");
        } else {
            map.put("code", 1);
            map.put("msg", "登录成功");
            map.put("adminId", admin.getId().toString());
            map.put("adminName", admin.getUsername());
            // 设置session
            session.setAttribute("adminId", admin.getId());
        }
        return map;
    }


    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Admin admin, HttpSession session) {
        HashMap<String, Object> map = new HashMap<>();

        // 判断 vcode
        String vcode = admin.getVcode();
        String s = redisTemplate.opsForValue().get(vcode);
        if(ObjectUtils.isEmpty(s)) {
            map.put("code", 0);
            map.put("msg", "验证码错误");
            return map;
        }

        // 加密后的密码
        String password = admin.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, admin.getUsername());
        admin = adminService.getOne(queryWrapper);

        if(admin == null) {
            map.put("code", 0);
            map.put("msg", "用户不存在");
        } else if(!Objects.equals(admin.getPassword(), password)) {
            map.put("code", 0);
            map.put("msg", "密码错误");
        } else {
            map.put("code", 1);
            map.put("msg", "登录成功");
            map.put("adminId", admin.getId().toString());
            map.put("adminName", admin.getUsername());
            // 设置session
            session.setAttribute("adminId", admin.getId());
        }
        return map;
    }
    @PostMapping("/update")
    public R<?> updateById(@RequestBody UpdateUser updateUser) {
        User user = new User();
        BeanUtils.copyProperties(updateUser, user);
        boolean b = userService.updateById(user);
        if(b) {
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }

    @GetMapping("/list")
    public R<List<User>> list() {
        return R.success(userService.list());
    }

    @GetMapping("/{id}")
    public R<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        user.setPassword(null);
        return R.success(user);
    }

    @GetMapping("/resetPassword")
    public R<?> resetPassword(@RequestParam("id") String id) {
        User user = new User();
        user.setId(Long.valueOf(id));
        user.setPassword(DigestUtils.md5DigestAsHex(Constants.initPassword.getBytes()));
        boolean b = userService.updateById(user);
        if(b) return R.success("密码重置成功");
        return R.error("重置失败");
    }

    @GetMapping("/deleteUser")
    public R<?> deleteUser(@RequestParam("id") Long id) {
        boolean b = userService.removeById(id);
        if(b) return R.success("删除成功");
        return R.error("删除失败");
    }

    @PostMapping("/add")
    public R<?> add(@RequestBody User user) {
        if(user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            return R.error("格式错误");
        }
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(password);
        boolean save = userService.save(user);
        if(save) return R.success("添加成功");
        return R.error("添加失败");
    }

}
