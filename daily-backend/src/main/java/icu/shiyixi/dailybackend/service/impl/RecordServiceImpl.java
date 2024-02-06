package icu.shiyixi.dailybackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.shiyixi.dailybackend.bean.Record;
import icu.shiyixi.dailybackend.mapper.RecordMapper;
import icu.shiyixi.dailybackend.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {

    @Autowired
    RecordMapper recordMapper;

    @Override
    public List<Record> getByUserDate(Long userId, String date) {
        return recordMapper.getByDateUser(userId, date);
    }

    @Override
    public List<Record> getByUser(Long userId) {
        return recordMapper.getByUser(userId);
    }
}
