package icu.shiyixi.dailybackend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.shiyixi.dailybackend.bean.Admin;
import icu.shiyixi.dailybackend.mapper.AdminMapper;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
}
