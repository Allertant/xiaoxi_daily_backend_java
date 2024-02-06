package icu.shiyixi.dailybackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.shiyixi.dailybackend.bean.Record;

import java.util.List;

public interface RecordService extends IService<Record> {
    public List<Record> getByUserDate(Long userId, String date);
    public List<Record> getByUser(Long userId);

}
