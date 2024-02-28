package icu.shiyixi.dailybackend.cache;

import com.alibaba.fastjson.JSON;
import icu.shiyixi.dailybackend.dto.plan.PlanObjectDto;
import icu.shiyixi.dailybackend.model.domain.Plan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PlansCache {
    @Resource
    private RedisTemplate<String, String> redisTemplate;


    public List<Plan> getCachePlans(Long userId) {
        String key = BaseCacheHeader.PLANS_CACHE + userId;
        String s = redisTemplate.opsForValue().get(key);

        List<Plan> plans = JSON.parseArray(s, Plan.class);
        if(plans != null) {
            log.info("获取cache成功，key: {}", key);
        }

        return plans;
    }

    public void setCachePlans(List<Plan> plans, Long userId) {
        String key = BaseCacheHeader.PLANS_CACHE + userId;
        String value = JSON.toJSONString(plans);

        redisTemplate.opsForValue().set(key, value, CacheContext.EXPIRE_TIME, TimeUnit.MINUTES);

        log.info("存储cache成功，key: {}", key);
    }

    public void removeCachePlans(Long userId) {
        String key = BaseCacheHeader.PLANS_CACHE + userId;
        redisTemplate.delete(key);
        log.info("删除cache成功，key: {}", key);
    }
}
