package icu.shiyixi.dailybackend.dto.plan;

import icu.shiyixi.dailybackend.model.domain.Plan;
import icu.shiyixi.dailybackend.model.domain.PlanDetail;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class PlanObjectDto extends Plan implements Serializable {

    private List<PlanDetail> details = new ArrayList<>();

    private static final long serialVersionUID = 1L;
}
