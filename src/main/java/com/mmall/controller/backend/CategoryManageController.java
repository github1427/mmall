package com.mmall.controller.backend;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 上午9:47 2018/6/19
 * @ Description：
 */

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "/add_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse addCategory(HttpServletRequest httpServletRequest, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
//        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
//        }
//        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
//        User user= JsonUtil.stringToObj(loginUserJson,User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()) {
//            return iCategoryService.addCategory(categoryName, parentId);
//        } else {
//            return ServerResponse.createByErrorMessage("用户无权限");
//        }
        //通过springMVC拦截器校验用户是否登录以及是否具有管理员权限
        return iCategoryService.addCategory(categoryName, parentId);

    }

    @RequestMapping(value = "/set_category_name.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse setCategoryName(Integer categoryId, String categoryName, HttpServletRequest httpServletRequest) {
//        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
//        }
//        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
//        User user= JsonUtil.stringToObj(loginUserJson,User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()) {
//            return iCategoryService.setCategoryName(categoryId, categoryName);
//        } else {
//            return ServerResponse.createByErrorMessage("用户无权限");
//        }
        //通过springMVC拦截器校验用户是否登录以及是否具有管理员权限
        return iCategoryService.setCategoryName(categoryId, categoryName);
    }

    @RequestMapping(value = "/get_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getCategoryAndParallelChildrenCategory(HttpServletRequest httpServletRequest, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
//        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
//        }
//        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
//        User user=JsonUtil.stringToObj(loginUserJson,User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()) {
//            return iCategoryService.getCategoryAndParallelChildrenCategory(categoryId);
//        } else {
//            return ServerResponse.createByErrorMessage("用户无权限");
//        }
        //通过springMVC拦截器校验用户是否登录以及是否具有管理员权限
        return iCategoryService.getCategoryAndParallelChildrenCategory(categoryId);
    }

    @RequestMapping(value = "/get_deep_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Integer>> getDeepCategory(HttpServletRequest httpServletRequest, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
//        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
//        }
//        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
//        User user= JsonUtil.stringToObj(loginUserJson,User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()) {
//            return iCategoryService.getDeepCategory(categoryId);
//        } else {
//            return ServerResponse.createByErrorMessage("用户无权限");
//        }
        //通过springMVC拦截器校验用户是否登录以及是否具有管理员权限
        return iCategoryService.getDeepCategory(categoryId);
    }

}
