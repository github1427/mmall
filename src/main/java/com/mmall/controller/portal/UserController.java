package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
            RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.objToString(response.getData()),Const.RedisCacheExtime.REDIS_EXTIME);
        }
        return response;
    }

    @RequestMapping(value = "/register.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse register(User user) {
        return iUserService.register(user);
    }

    @RequestMapping(value = "/check_valid.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    @RequestMapping(value = "/logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse logout(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
        RedisShardedPoolUtil.del(loginToken);
        return ServerResponse.createBySuccessMessage("退出成功");
    }

    @RequestMapping(value = "/get_user_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest httpServletRequest) {
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
        User current_user=JsonUtil.stringToObj(loginUserJson,User.class);
        if (current_user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        } else {
            return ServerResponse.createBySuccess(current_user);
        }
    }

    @RequestMapping(value = "/forget_get_question.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.forgetGetQuestion(username);
    }

    @RequestMapping(value = "/forget_check_answer.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.forgetCheckAnswer(username, question, answer);
    }

    @RequestMapping(value = "/forget_reset_password.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    @RequestMapping(value = "/reset_password.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse resetPassword(String passwordOld, String passwordNew, HttpServletRequest httpServletRequest) {
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(loginUserJson,User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld, passwordNew, user);
    }

    @RequestMapping(value = "/update_information.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> updateInformation(User user, HttpServletRequest httpServletRequest) {
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
        User currentUser=JsonUtil.stringToObj(loginUserJson,User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setUsername(currentUser.getUsername());
        user.setId(currentUser.getId());
        ServerResponse<User> serverResponse = iUserService.updateInformation(user);
        if (serverResponse.isSuccess()) {
            String newLoginUserJson=JsonUtil.objToString(user);
            RedisShardedPoolUtil.setEx(loginToken,newLoginUserJson,Const.RedisCacheExtime.REDIS_EXTIME);
        }
        return serverResponse;
    }

    @RequestMapping(value = "/get_information.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpServletRequest httpServletRequest) {
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(loginUserJson,User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,无法获取当前用户信息,status=10,强制登录");
        }
        return iUserService.getInformation(user.getId());
    }

}
