package icu.shiyixi.dailybackend.filter;

import com.alibaba.fastjson.JSON;
import icu.shiyixi.dailybackend.common.BaseContext;
import icu.shiyixi.dailybackend.common.R;
import icu.shiyixi.dailybackend.token.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * ClassName : LoginCheckFilter
 * Description: 检查用户是否已经完成登录
 *
 * @Author : shiyixi
 * @Create : 2023/8/10 13:12
 */
@Slf4j
@WebFilter(filterName = "Filter02", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {


    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //拦截器取到请求先进行判断，如果是OPTIONS请求，则放行
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("Method:OPTIONS");
            filterChain.doFilter(request, response);
        }

        // 1. 获取本次请求的uri
        String requestURI = request.getRequestURI();
        // HttpSession session = request.getSession();

        // 静态资源和登录相关的不需要处理
        String[] urls = new String[]{
                "/",
                "/user/login",
                "/common/vcode",
                "/admin/login",
                "/common/downloadApk",
                "/common/code",
                "/admin/loginByCode"
        };
        log.info("session adminId: {}", request.getSession().getAttribute("adminId"));
        // 2. 判断本次请求需要处理
        boolean check = check(urls, requestURI);

        // 3. 如果不需要处理，则直接放行
        if(check) {
            log.info("本次请求{}不需要要处理", request.getRequestURI());
            filterChain.doFilter(request, response);
            return ;
        }

        // 4-1. 判断登录状态，针对移动端用户，使用jwt字符串进行验证
        String jwt = request.getHeader("jwt");
        if(jwt != null) {
            log.info("这是前端请求");
            boolean verify = TokenUtils.verify(jwt);
            if(verify) {
                BaseContext.setCurrentId(TokenUtils.detoken(jwt));
                filterChain.doFilter(request, response);
                return ;
            }
        }


        // 4-2. 判断登录状态，如果已经登录，则直接放行（针对管理端）
        if(request.getSession().getAttribute("adminId") != null) {
            Long adminId = (Long) request.getSession().getAttribute("adminId");
            // 保存id数据到threadlocal中
            BaseContext.setCurrentId(adminId);
            // log.info("管理员已登录，管理员id为：{}", request.getSession().getAttribute("adminId"));
            filterChain.doFilter(request, response);
            return ;
        }

        /**
         * .allowCredentials(true)
         *                 .allowedHeaders("*")
         *                 .allowedMethods("*")
         *                 .allowedOriginPatterns("*");
         */
        // 5. 如果未登录则返回未登录结果，通过输出流对象向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
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
