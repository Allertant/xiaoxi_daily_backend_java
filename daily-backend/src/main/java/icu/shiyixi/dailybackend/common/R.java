package icu.shiyixi.dailybackend.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class R<T> implements Serializable {

    private int code; //编码：0成功，其它数字为失败

    private T data; //数据

    private String msg; //错误信息

    private String description; // 描述信息


    /**
     * 成功的处理伙计
     * @param data 返回的数据
     * @return
     */
    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.code = 0;
        r.data = data;
        r.msg = "ok";
        r.description = "ok";
        return r;
    }

    /**
     * 成功的处理伙计
     * @param data 返回的数据
     * @param msg 返回的消息
     * @return R 对象
     */
    public static <T> R<T> success(T data, String msg) {
        R<T> r = new R<>();
        r.code = 0;
        r.data = data;
        r.msg = msg;
        r.description = "ok";
        return r;
    }

    public static R<?> error(int code, String msg, String description) {
        R<?> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setDescription(description);
        return r;
    }

    public static R<?> error(ErrorCode errorCode) {
        R<?> r = new R<>();
        r.setCode(errorCode.getCode());
        r.setMsg(errorCode.getMessage());
        r.setDescription(errorCode.getDescription());
        return r;
    }

    public static R<?> error(ErrorCode errorCode, String msg, String description) {
        R<?> r = new R<>();
        r.setCode(errorCode.getCode());
        r.setMsg(msg);
        r.setDescription(description);
        return r;
    }

    public static R<?> error(ErrorCode errorCode, String description) {
        R<?> r = new R<>();
        r.setCode(errorCode.getCode());
        r.setMsg(errorCode.getMessage());
        r.setDescription(description);
        return r;
    }



}
