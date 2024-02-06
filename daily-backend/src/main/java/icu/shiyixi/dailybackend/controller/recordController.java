package icu.shiyixi.dailybackend.controller;

import icu.shiyixi.dailybackend.bean.Arrangement;
import icu.shiyixi.dailybackend.bean.Record;
import icu.shiyixi.dailybackend.common.BaseContext;
import icu.shiyixi.dailybackend.service.ArrangementService;
import icu.shiyixi.dailybackend.service.RecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/record")
public class recordController {
    @Autowired
    RecordService recordService;
    @Autowired
    ArrangementService arrangementService;
    @GetMapping("/history")
    public Map<String, Object> history() {
        Long userId = BaseContext.getCurrentId();

        List<Record> records = recordService.getByUser(userId);
        List<Arrangement> arrangements = arrangementService.getByUserId(userId);

        records.forEach(item -> {
            Long arrangement = item.getArrangement();
            arrangements.forEach(arra -> {
                if(arrangement==arra.getId()) {
                    item.setBeginTime(arra.getBeginTime());
                    item.setEndTime(arra.getEndTime());
                }
            });
        });

        HashMap<String, Object> map = new HashMap<>();
        map.put("data", records);
        map.put("code", "1");
        return map;
    }
}
