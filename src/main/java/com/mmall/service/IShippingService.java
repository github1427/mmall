package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

import java.util.Map;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午12:23 2018/6/24
 * @ Description：收货地址功能接口
 */
public interface IShippingService {
    ServerResponse<Map<String,Integer>> addShipping(Integer userId, Shipping shipping);

    ServerResponse deleteShipping(Integer userId,Integer shippingId);

    ServerResponse updateShipping(Integer userId,Shipping shipping);

    ServerResponse<Shipping> selectShipping(Integer userId,Integer shippingId);

    ServerResponse<PageInfo> listShipping(Integer userId, Integer pageNum, Integer pageSize);
}
