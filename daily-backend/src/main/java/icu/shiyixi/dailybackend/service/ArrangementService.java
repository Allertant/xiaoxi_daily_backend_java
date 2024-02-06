package icu.shiyixi.dailybackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.shiyixi.dailybackend.bean.Arrangement;

import java.util.List;

public interface ArrangementService extends IService<Arrangement> {
    List<Arrangement> getByUserId(Long userId);

}
