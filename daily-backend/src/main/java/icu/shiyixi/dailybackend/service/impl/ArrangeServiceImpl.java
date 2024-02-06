package icu.shiyixi.dailybackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.shiyixi.dailybackend.bean.Arrangement;
import icu.shiyixi.dailybackend.mapper.ArrangementMapper;
import icu.shiyixi.dailybackend.service.ArrangementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArrangeServiceImpl extends ServiceImpl<ArrangementMapper, Arrangement> implements ArrangementService {

    @Autowired
    ArrangementMapper arrangementMapper;

    @Override
    public List<Arrangement> getByUserId(Long userId) {
        return arrangementMapper.getByUserId(userId);
    }

}
