package icu.shiyixi.dailybackend.dto;

import icu.shiyixi.dailybackend.bean.PlanDetail;
import lombok.Data;

@Data
public class PlanDetailsDto extends PlanDetail {
    private String msg;
    private int code;
}
