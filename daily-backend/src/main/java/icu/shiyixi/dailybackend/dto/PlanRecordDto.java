package icu.shiyixi.dailybackend.dto;

import icu.shiyixi.dailybackend.bean.PlanRecord;
import lombok.Data;

import java.time.LocalTime;

@Data
public class PlanRecordDto extends PlanRecord {
    private LocalTime beginTime;
    private LocalTime endTime;
    private int orderNum;
}
