package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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
    public ServerResponse<PageInfo> manageOrderList(HttpSession session,
                                                    @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                                    @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageOrderList(pageNum,pageSize);
        } else {
            return ServerResponse.createByErrorMessage("用户无权限");
        }

    }

    @RequestMapping("/search")
    @ResponseBody
    public ServerResponse<PageInfo> manageOrderSearch(HttpSession session,
                                                    Long orderNo,
                                                    @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                                    @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageOrderSearch(orderNo,pageNum,pageSize);
        } else {
            return ServerResponse.createByErrorMessage("用户无权限");
        }

    }

    @RequestMapping("/detail")
    @ResponseBody
    public ServerResponse<OrderVo> manageOrderDetail(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageOrderDetail(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("用户无权限");
        }

    }

    @RequestMapping("/send_goods")
    @ResponseBody
    public ServerResponse manageSendGoods(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageSendGoods(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("用户无权限");
        }

    }


}
