package icu.shiyixi.dailybackend.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import icu.shiyixi.dailybackend.bean.Arrangement;
import icu.shiyixi.dailybackend.bean.Record;
import icu.shiyixi.dailybackend.common.BaseContext;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.service.ArrangementService;
import icu.shiyixi.dailybackend.service.RecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/arrangement")
public class ArrangeController {
    @Autowired
    ArrangementService arrangementService;

    @Autowired
    RecordService recordService;

    @GetMapping("/one/{id}")
    public List<Arrangement> one(@PathVariable Long id) {
        return arrangementService.getByUserId(id);
    }

    @GetMapping("/id/{id}")
    public R<Arrangement> id(@PathVariable Long id) {
        return R.success(arrangementService.getById(id));
    }

    @PostMapping("/id/{id}")
    public R<?> id(@PathVariable Long id, @RequestBody Arrangement arrangement) {
        arrangement.setId(id);
        boolean b = arrangementService.updateById(arrangement);
        if(b) return R.success("更新成功");
        return R.error("更新失败");
    }

    @DeleteMapping("/id/{id}")
    public R<?> deleteById(@PathVariable Long id) {
        boolean b = arrangementService.removeById(id);
        if(b) return R.success("删除成功");
        return R.error("删除失败");
    }

    @PostMapping("/insert")
    public R<?> insert( @RequestBody Arrangement arrangement) {
        boolean b = arrangementService.save(arrangement);
        if(b) return R.success("新增成功");
        return R.error("新增失败");
    }

    @GetMapping ("/list")
    public List<Arrangement> list() {
        Long userId = BaseContext.getCurrentId();
        // 查询总的日程
        List<Arrangement> arrangementList = arrangementService.getByUserId(userId);

        // 查询当天的日程
        List<Record> recordList = recordService.getByUserDate(userId, LocalDate.now().toString());

        // 重新更新日程数据
        arrangementList.forEach(item -> {
            Long arrangementId = item.getId();

            // 时间范围
            LocalTime now = LocalTime.now();
            LocalTime beginTime = item.getBeginTime();
            LocalTime endTime = item.getEndTime();

            // 是否已经被记录了
            boolean inRecord = false;
            for (Record record : recordList) {
                if(Objects.equals(record.getArrangement(), arrangementId)) {
                    inRecord = true;
                    break;
                }
            }

            if(now.isBefore(beginTime)) {
                item.setCode(1001);
                item.setMsg("未开始");
            }

            else if(now.isAfter(beginTime) && now.isBefore(endTime) && !inRecord) {
                item.setCode(1002);
                item.setMsg("开始");
            }

            else if(now.isAfter(beginTime) && now.isBefore(endTime) && inRecord) {
                item.setCode(1003);
                item.setMsg("进行中");
            }

            else if(now.isAfter(endTime) && !inRecord) {
                item.setCode(1004);
                item.setMsg("已结束");
            }

            else if(now.isAfter(endTime) && inRecord) {
                item.setCode(1005);
                item.setMsg("已完成");
            }
        });
        return arrangementList;
    }

    @GetMapping("/add")
    public R<String> add(Long arrangement) {
        Record record = new Record();
        record.setArrangement(arrangement);
        recordService.save(record);
        return R.success("更新成功");
    }
}
