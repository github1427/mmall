package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午3:49 2018/6/27
 * @ Description：后台 订单处理器
 */

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {
    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private IUserService iUserService;

    @RequestMapping("/list")
    @ResponseBody
    public ServerResponse<PageInfo> manageOrderList(HttpServletRequest httpServletRequest,
                                                    @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                                    @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize) {
//        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
//        }
//        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
//        User user= JsonUtil.stringToObj(loginUserJson,User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()) {
//            return iOrderService.manageOrderList(pageNum,pageSize);
//        } else {
//            return ServerResponse.createByErrorMessage("用户无权限");
//        }
        //通过springMVC拦截器校验用户是否登录以及是否具有管理员权限
        return iOrderService.manageOrderList(pageNum,pageSize);

    }

    @RequestMapping("/search")
    @ResponseBody
    public ServerResponse<PageInfo> manageOrderSearch(HttpServletRequest httpServletRequest,
                                                    Long orderNo,
                                                    @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                                    @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize) {
//        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
//        }
//        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
//        User user= JsonUtil.stringToObj(loginUserJson,User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()) {
//            return iOrderService.manageOrderSearch(orderNo,pageNum,pageSize);
//        } else {
//            return ServerResponse.createByErrorMessage("用户无权限");
//        }

        //通过springMVC拦截器校验用户是否登录以及是否具有管理员权限
        return iOrderService.manageOrderSearch(orderNo,pageNum,pageSize);

    }

    @RequestMapping("/detail")
    @ResponseBody
    public ServerResponse<OrderVo> manageOrderDetail(HttpServletRequest httpServletRequest, Long orderNo) {
//        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
//        }
//        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
//        User user=JsonUtil.stringToObj(loginUserJson,User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()) {
//            return iOrderService.manageOrderDetail(orderNo);
//        } else {
//            return ServerResponse.createByErrorMessage("用户无权限");
//        }

        //通过springMVC拦截器校验用户是否登录以及是否具有管理员权限
        return iOrderService.manageOrderDetail(orderNo);

    }

    @RequestMapping("/send_goods")
    @ResponseBody
    public ServerResponse manageSendGoods(HttpServletRequest httpServletRequest, Long orderNo) {
//        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
//        }
//        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
//        User user= JsonUtil.stringToObj(loginUserJson,User.class);
//        if (user == null) {
//            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
//        }
//        if (iUserService.checkAdminRole(user).isSuccess()) {
//            return iOrderService.manageSendGoods(orderNo);
//        } else {
//            return ServerResponse.createByErrorMessage("用户无权限");
//        }

        //通过springMVC拦截器校验用户是否登录以及是否具有管理员权限
        return iOrderService.manageSendGoods(orderNo);

    }


}
