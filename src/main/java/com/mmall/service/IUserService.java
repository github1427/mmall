package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午12:28 2018/6/17
 * @ Description：用户模块功能接口
 */

public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse checkValid(String str, String type);

    ServerResponse register(User user);

    ServerResponse<String> forgetGetQuestion(String username);

    ServerResponse<String> forgetCheckAnswer(String username, String question, String answer);

    ServerResponse forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse resetPassword(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);

    ServerResponse checkAdminRole(User user);
}
