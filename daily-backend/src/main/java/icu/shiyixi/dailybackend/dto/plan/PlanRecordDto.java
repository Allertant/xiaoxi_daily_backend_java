package icu.shiyixi.dailybackend.dto.plan;

import icu.shiyixi.dailybackend.model.domain.PlanRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
public class PlanRecordDto extends PlanRecord {
    private LocalTime beginTime;
    private LocalTime endTime;
    private int orderNum;
}
