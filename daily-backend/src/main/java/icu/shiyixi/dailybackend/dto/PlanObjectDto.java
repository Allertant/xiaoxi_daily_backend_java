package icu.shiyixi.dailybackend.dto;

import icu.shiyixi.dailybackend.bean.Plan;
import icu.shiyixi.dailybackend.bean.PlanDetail;
import lombok.Data;

import java.util.List;

@Data
public class PlanObjectDto extends Plan {
    private List<PlanDetail> details;
}
