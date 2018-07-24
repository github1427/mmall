package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午1:50 2018/6/26
 * @ Description：订单功能接口包含支付
 */
public interface IOrderService {
    ServerResponse<Map<String,String>> payOrder(Long orderNo, Integer userId, String path);

    ServerResponse checkAliCallback(Map<String,String> params);

    ServerResponse<Boolean> queryOrderPayStatus(Integer userId,Long orderNo);

    ServerResponse createOrder(Integer userId,Integer shippingId);

    ServerResponse cancelOrder(Integer userId,Long orderNo);

    ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId);

    ServerResponse<PageInfo> orderList(Integer userId, Integer pageNum, Integer pageSize);

    ServerResponse<OrderVo> orderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo> manageOrderList(Integer pageNum,Integer pageSize);

    ServerResponse<PageInfo> manageOrderSearch(Long orderNo,Integer pageNum,Integer pageSize);

    ServerResponse<OrderVo> manageOrderDetail(Long orderNo);

    ServerResponse manageSendGoods(Long orderNo);

    void closeOrderByHour(Integer hours);
}
