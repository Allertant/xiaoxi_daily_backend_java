package icu.shiyixi.dailybackend.filter;

import com.alibaba.fastjson.JSON;
import icu.shiyixi.dailybackend.common.BaseContext;
import icu.shiyixi.dailybackend.common.ErrorCode;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.dto.user.UserRegisterResDto;
import icu.shiyixi.dailybackend.exception.BusinessException;
import icu.shiyixi.dailybackend.token.TokenUtils;
import icu.shiyixi.dailybackend.utils.CookieSetterUtils;
import jdk.nashorn.internal.parser.Token;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import sun.security.ssl.CookieExtension;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

/**
 * ClassName : LoginCheckFilter
 * Description: 检查用户是否已经完成登录
 *
 * @Author : shiyixi
 * @Create : 2023/8/10 13:12
 */
@Slf4j
@WebFilter(filterName = "Filter01", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {


    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //拦截器取到请求先进行判断，如果是OPTIONS请求，则放行
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
        }

        // 1. 获取本次请求的uri
        String requestURI = request.getRequestURI();

        // 静态资源和登录相关的不需要处理
        String[] urls = new String[]{
                "/",
                "/user/login",
                "/user/register",
                "/common/vcode",
                "/admin/login",
                "/common/downloadApk",
                "/common/code",
                "/admin/loginByCode"
        };

        // 2. 判断本次请求需要处理
        boolean check = check(urls, requestURI);

        // 3. 如果不需要处理，则直接放行
        if(check) {
            log.info("本次请求{}不需要要处理", request.getRequestURI());
            filterChain.doFilter(request, response);
            return ;
        }

        // 4. 判断登录状态，针对移动端用户，使用jwt字符串进行验证
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            log.info("cookie: {}", cookie.getName());
            if (Objects.equals(cookie.getName(), "jwt")) {
                String jwt = cookie.getValue();
                if (!StringUtils.isAnyBlank(jwt)) {
                    boolean verify = TokenUtils.verify(jwt);
                    if (verify) {
                        // 验证成功
                        Long userId = TokenUtils.detoken(jwt);
                        BaseContext.setCurrentId(userId);
                        log.info("用户 {} 登录成功", userId);
                        filterChain.doFilter(request, response);
                        // 重新设置cookie
                        CookieSetterUtils.setCookie(response, userId);
                        return;
                    }
                }
                break;
            }
        }

        // 5. 如果未登录则直接报错
        throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match) {
                return true;
            }
        }
        return false;
    }
}
