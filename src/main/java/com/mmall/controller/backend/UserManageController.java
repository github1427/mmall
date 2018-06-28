package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午8:03 2018/6/17
 * @ Description：
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> serverResponse=iUserService.login(username,password);
        if (serverResponse.isSuccess()){
            if (serverResponse.getData().getRole()!= Const.Role.ROLE_ADMIN){
                return ServerResponse.createByErrorMessage("不是管理员，无法登陆");
            }
            session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
        }
        return serverResponse;

    }
}
