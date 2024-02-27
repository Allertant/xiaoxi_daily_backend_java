package icu.shiyixi.dailybackend.exception;

import icu.shiyixi.dailybackend.common.ErrorCode;
import icu.shiyixi.dailybackend.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public R<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e);
        return R.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public R<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return R.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "服务器错误");
    }
}
