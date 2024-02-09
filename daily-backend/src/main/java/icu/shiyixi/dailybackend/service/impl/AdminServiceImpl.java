package icu.shiyixi.dailybackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.shiyixi.dailybackend.bean.Admin;
import icu.shiyixi.dailybackend.mapper.AdminMapper;
import icu.shiyixi.dailybackend.service.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
}
