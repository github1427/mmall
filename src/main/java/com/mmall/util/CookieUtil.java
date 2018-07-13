package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午12:23 2018/7/10
 * @ Description：
 */
@Slf4j
public class CookieUtil {
    private static final String COOKIE_DOMAIN=".layman.top";
    private static final String COOKIE_NAME="login_token";

    public static void writeLoginToken(HttpServletResponse response,String token){
        /**
         * create by: vain
         * description: 写cookie
         * create time: 下午12:41 2018/7/10
         *
         * @Param: response
         * @Param: token
         * @return void
         */
        Cookie cookie=new Cookie(COOKIE_NAME,token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60*60*24);//单位是秒，-1代表永久,如果maxage不设置的话，cookie就不会写入硬盘，则会写入内存，只在当前页面有效
        log.info("write cookieName:{} cookieValue:{}",cookie.getName(),cookie.getValue());
        response.addCookie(cookie);
    }

    public static String readLoginToken(HttpServletRequest request){
        /**
         * create by: vain
         * description: 读cookie
         * create time: 下午12:41 2018/7/10
         *
         * @Param: request
         * @return java.lang.String
         */
        Cookie[] cookies=request.getCookies();
        if (cookies!=null){
            for (Cookie cookie:cookies){
                log.info("read cookieName:{} cookieValue:{}",cookie.getName(),cookie.getValue());
                if (StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                    log.info("return cookieName:{} cookieValue:{}",cookie.getName(),cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        /**
         * create by: vain
         * description: 删除cookie
         * create time: 下午12:42 2018/7/10
         *
         * @Param: request
         * @Param: response
         * @return void
         */
        Cookie [] cookies=request.getCookies();
        if (cookies!=null){
            for (Cookie cookie:cookies){
                if (StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                    cookie.setPath("/");
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setMaxAge(0);//设置成0代表删除此cookie
                    log.info("delete cookieName:{} cookieValue:{}",cookie.getName(),cookie.getValue());
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }
}
