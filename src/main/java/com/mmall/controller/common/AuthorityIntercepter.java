package com.mmall.controller.common;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午2:23 2018/7/13
 * @ Description：springMVC拦截器 权限校验
 */
public class AuthorityIntercepter implements HandlerInterceptor{
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HandlerMethod handlerMethod= (HandlerMethod) o;
        String methodName=handlerMethod.getMethod().getName();
        String className=handlerMethod.getBean().getClass().getSimpleName();
        StringBuffer requestParam=new StringBuffer();
        Map requestParamMap=httpServletRequest.getParameterMap();
        Iterator iterator=requestParamMap.keySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry= (Map.Entry) iterator.next();
            String keyName= (String) entry.getKey();
            String keyValue= StringUtils.EMPTY;
            Object object=entry.getValue();
            if (object instanceof String[]){
                keyValue= Arrays.toString((String[]) object);
            }
            requestParam.append(keyName).append("=").append(keyValue);
        }

        User user=null;
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)){
            String userJson = RedisShardedPoolUtil.get(loginToken);
            user= JsonUtil.stringToObj(userJson,User.class);
        }
        if (user==null||user.getRole()!= Const.Role.ROLE_ADMIN){
            httpServletResponse.reset();
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            PrintWriter out=httpServletResponse.getWriter();
            if (user==null){
                if (StringUtils.equals(className, "ProductManageController")&&StringUtils.equals(methodName,"richtextUpload")){
                    Map map= Maps.newHashMap();
                    map.put("success",false);
                    map.put("msg","用户未登陆，请登录管理员");
                    out.print(JsonUtil.objToString(map));
                }else {
                    out.print(JsonUtil.objToString(ServerResponse.createByErrorMessage("用户未登陆，请登录")));
                }
            }else {
                if (StringUtils.equals(className, "ProductManageController")&&StringUtils.equals(methodName,"richtextUpload")){
                    Map map= Maps.newHashMap();
                    map.put("success",false);
                    map.put("msg","用户无权限");
                    out.print(JsonUtil.objToString(map));
                }else {
                    out.print(JsonUtil.objToString(ServerResponse.createByErrorMessage("用户无管理员权限，登录失败")));
                }
            }
            out.flush();
            out.close();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
