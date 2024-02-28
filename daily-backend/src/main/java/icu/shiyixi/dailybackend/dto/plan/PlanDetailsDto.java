package icu.shiyixi.dailybackend.dto.plan;

import icu.shiyixi.dailybackend.model.domain.PlanDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class PlanDetailsDto extends PlanDetail {
    private String msg;
    private int code;
}
