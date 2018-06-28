package com.mmall.service.Impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午12:28 2018/6/17
 * @ Description：用户模块功能实现
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        /**
         * create by: vain
         * description: 用户登录功能实现
         * create time: 下午1:34 2018/6/17
         *
         * @Param: username
         * @Param: password
         * @return com.mmall.common.ServerResponse<com.mmall.pojo.User>
         */
        int count = userMapper.checkUsername(username);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User loginUser = userMapper.selectLogin(username, md5Password);
        if (loginUser == null) {
            return ServerResponse.createByErrorMessage("用户名密码错误");
        }
        loginUser.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", loginUser);
    }

    public ServerResponse register(User user) {
        /**
         * create by: vain
         * description: 注册功能实现
         * create time: 下午2:07 2018/6/17
         *
         * @Param: user
         * @return com.mmall.common.ServerResponse
         */
        ServerResponse response = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!response.isSuccess()) {
            return response;
        }
        response = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!response.isSuccess()) {
            return response;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int count = userMapper.insert(user);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        } else {
            return ServerResponse.createBySuccessMessage("注册成功");
        }
    }

    public ServerResponse checkValid(String str, String type) {
        /**
         * create by: vain
         * description: 根据前端传递过来的参数，校验用户名或邮箱
         * create time: 下午1:52 2018/6/17
         *
         * @Param: str
         * @Param: type
         * @return com.mmall.common.ServerResponse
         */
        if (StringUtils.isNotBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int count = userMapper.checkUsername(str);
                if (count > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int count = userMapper.checkEmail(str);
                if (count > 0) {
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse<String> forgetGetQuestion(String username) {
        /**
         * create by: vain
         * description: 获取用户设置的密保问题
         * create time: 下午4:20 2018/6/17
         *
         * @Param: username
         * @return com.mmall.common.ServerResponse<java.lang.String>
         */
        ServerResponse response = this.checkValid(username, Const.USERNAME);
        if (response.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.forgetGetQuestion(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        } else {
            return ServerResponse.createByErrorMessage("用户未设置找回密码问题");
        }
    }

    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        /**
         * create by: vain
         * description: 验证密保问题答案是否正确，若正确返回一个有时效的token
         * create time: 下午4:29 2018/6/17
         *
         * @Param: username
         * @Param: question
         * @Param: answer
         * @return com.mmall.common.ServerResponse<java.lang.String>
         */
        int count = userMapper.forgetCheckAnswer(username, question, answer);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("问题答案错误");
        }
        String token = UUID.randomUUID().toString();
        TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, token);
        return ServerResponse.createBySuccess(token);
    }

    public ServerResponse forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        ServerResponse response = this.checkValid(username, Const.USERNAME);
        if (response.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token已失效");
        }
        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int count = userMapper.updatePasswordByUsername(username, md5Password);
            if (count > 0) {
                return ServerResponse.createBySuccessMessage("密码修改成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取");
        }
        return ServerResponse.createByErrorMessage("密码修改失败");
    }

    public ServerResponse resetPassword(String passwordOld, String passwordNew, User user) {
        /**
         * create by: vain
         * description: 登录状态下修改密码
         * create time: 下午6:55 2018/6/17
         *
         * @Param: passwordOld
         * @Param: passwordNew
         * @Param: user
         * @return com.mmall.common.ServerResponse
         */
        int count = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (count == 0) {
            return ServerResponse.createByErrorMessage("旧密码输入错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        count = userMapper.updateByPrimaryKeySelective(user);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("修改密码失败");
        } else {
            return ServerResponse.createBySuccessMessage("修改密码成功");
        }
    }

    public ServerResponse<User> updateInformation(User user) {
        /**
         * create by: vain
         * description: 更新个人信息
         * create time: 下午7:26 2018/6/17
         *
         * @Param: user
         * @return com.mmall.common.ServerResponse<com.mmall.pojo.User>
         */
        int count = userMapper.checkEmailById(user.getEmail(), user.getId());
        if (count > 0) {
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }
        count = userMapper.updateByPrimaryKeySelective(user);
        if (count > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", user);
        } else {
            return ServerResponse.createByErrorMessage("更新个人信息失败");
        }
    }

    public ServerResponse<User> getInformation(Integer userId) {
        /**
         * create by: vain
         * description: 获取当前登录用户的详细信息
         * create time: 下午7:57 2018/6/17
         *
         * @Param: userId
         * @return com.mmall.common.ServerResponse<com.mmall.pojo.User>
         */
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    //backend
    public ServerResponse checkAdminRole(User user){
        /**
         * create by: vain
         * description: 检查用户是否有管理员权限
         * create time: 上午10:05 2018/6/19
         *
         * @Param: user
         * @return com.mmall.common.ServerResponse
         */
        if (user!=null&&user.getRole()==Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
