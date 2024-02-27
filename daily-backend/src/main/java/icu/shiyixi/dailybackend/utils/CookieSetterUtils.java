package icu.shiyixi.dailybackend.utils;

import icu.shiyixi.dailybackend.token.TokenUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieSetterUtils {
    public static void setCookie(HttpServletResponse response, Long userId) {
        String jwt = TokenUtils.entoken(userId);
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7*2460*60); // 默认保存时间为1星期
        // 保存 cookie
        response.addCookie(cookie);
    }
}
