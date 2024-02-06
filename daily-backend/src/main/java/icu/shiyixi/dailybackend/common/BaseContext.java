package icu.shiyixi.dailybackend.common;

/**
 * ClassName : BaseContext
 * Description: 基于threadlocal封装的工具类，用于保存和获取当前登录用户的id
 *
 * @Author : shiyixi
 * @Create : 2023/8/11 19:15
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

}
