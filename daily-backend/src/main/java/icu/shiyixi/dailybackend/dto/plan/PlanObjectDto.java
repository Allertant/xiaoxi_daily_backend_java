package icu.shiyixi.dailybackend.dto.plan;

import icu.shiyixi.dailybackend.model.domain.Plan;
import icu.shiyixi.dailybackend.model.domain.PlanDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlanObjectDto extends Plan {
    private List<PlanDetail> details;
}
