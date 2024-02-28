package icu.shiyixi.dailybackend.cache;

import com.alibaba.fastjson.JSON;
import icu.shiyixi.dailybackend.dto.plan.PlanRecordDto;
import icu.shiyixi.dailybackend.model.domain.Plan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PlanHistoryCache {
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public List<PlanRecordDto> getCachePlanHistory(Long planId) {
        String key = BaseCacheHeader.PLAN_HISTORY_CACHE + planId;
        String s = redisTemplate.opsForValue().get(key);

        List<PlanRecordDto> planRecordDtos = JSON.parseArray(s, PlanRecordDto.class);
        if(planRecordDtos != null) {
            log.info("获取cache成功，key: {}", key);
        }

        return planRecordDtos;
    }

    public void setCachePlanHistory(List<PlanRecordDto> planRecordDtos, Long planId) {
        String key = BaseCacheHeader.PLAN_HISTORY_CACHE + planId;
        String value = JSON.toJSONString(planRecordDtos);

        redisTemplate.opsForValue().set(key, value, CacheContext.EXPIRE_TIME, TimeUnit.MINUTES);

        log.info("存储cache成功，key: {}", key);
    }

    public void removeCachePlanHistory(Long planId) {
        String key = BaseCacheHeader.PLAN_HISTORY_CACHE + planId;
        redisTemplate.delete(key);
        log.info("删除cache成功，key: {}", key);
    }
}
