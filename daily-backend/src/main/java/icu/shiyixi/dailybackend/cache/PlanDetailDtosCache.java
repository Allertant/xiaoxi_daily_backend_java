package icu.shiyixi.dailybackend.cache;

import com.alibaba.fastjson.JSON;
import icu.shiyixi.dailybackend.dto.plan.PlanDetailsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PlanDetailDtosCache {
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public List<PlanDetailsDto> getCachePlanDetailDtos(Long planId) {
        String key = BaseCacheHeader.PLAN_DETAIL_DTOS_CACHE + planId;
        String s = redisTemplate.opsForValue().get(key);

        List<PlanDetailsDto> planDetailsDtos = JSON.parseArray(s, PlanDetailsDto.class);
        if(planDetailsDtos != null) {
            log.info("获取cache成功，key: {}", key);
        }

        return planDetailsDtos;
    }

    public void setCachePlanDetailDtos(List<PlanDetailsDto> planDetailsDtos, Long planId) {
        String key = BaseCacheHeader.PLAN_DETAIL_DTOS_CACHE + planId;
        String value = JSON.toJSONString(planDetailsDtos);

        redisTemplate.opsForValue().set(key, value, CacheContext.EXPIRE_TIME, TimeUnit.MINUTES);

        log.info("存储cache成功，key: {}", key);
    }

    public void removeCachePlanDetailDtos(Long planId) {
        String key = BaseCacheHeader.PLAN_DETAIL_DTOS_CACHE + planId;
        redisTemplate.delete(key);
        log.info("删除cache成功，key: {}", key);
    }
}
