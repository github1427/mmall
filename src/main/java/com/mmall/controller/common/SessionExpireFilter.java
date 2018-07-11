package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 上午10:08 2018/7/11
 * @ Description：重置redis保存的登录对象的有效时间的过滤器
 */
public class SessionExpireFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest= (HttpServletRequest) servletRequest;
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)){
            String loginUserJson= RedisPoolUtil.get(loginToken);
            User loginUser= JsonUtil.stringToObj(loginUserJson,User.class);
            if (loginUser!=null){
                RedisPoolUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_EXTIME);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
