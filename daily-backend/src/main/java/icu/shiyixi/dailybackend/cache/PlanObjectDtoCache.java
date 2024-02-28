package icu.shiyixi.dailybackend.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import icu.shiyixi.dailybackend.common.ErrorCode;
import icu.shiyixi.dailybackend.dto.plan.PlanObjectDto;
import icu.shiyixi.dailybackend.exception.BusinessException;
import icu.shiyixi.dailybackend.model.domain.Plan;
import icu.shiyixi.dailybackend.model.domain.PlanDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.xml.transform.Source;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PlanObjectDtoCache {
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public PlanObjectDto getCachePlanObjectDto(Long planId) {
        String key = BaseCacheHeader.PLAN_OBJECT_CACHE + planId;
        String s = redisTemplate.opsForValue().get(key);

        PlanObjectDto planObjectDto = JSON.parseObject(s, PlanObjectDto.class);
        if(planObjectDto != null) {
            log.info("获取cache成功，key: {}", key);
        }

        return planObjectDto;
    }

    public void setCachePlanObjectDto(PlanObjectDto planObjectDto) {
        String key = BaseCacheHeader.PLAN_OBJECT_CACHE + planObjectDto.getId();
        String value = JSON.toJSONString(planObjectDto);

        redisTemplate.opsForValue().set(key, value, CacheContext.EXPIRE_TIME, TimeUnit.MINUTES);

        log.info("存储cache成功，key: {}", key);
    }

    public void removeCachePlanObjectDto(Long planId) {
        String key = BaseCacheHeader.PLAN_OBJECT_CACHE + planId;
        redisTemplate.delete(key);
        log.info("删除cache成功，key: {}", key);
    }
}
