package com.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午12:24 2018/6/24
 * @ Description：收货地址功能接口实现
 */

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService{

    @Autowired
    ShippingMapper shippingMapper;

    public ServerResponse<Map<String,Integer>> addShipping(Integer userId, Shipping shipping){
        /**
         * create by: vain
         * description: 新建地址
         * create time: 下午12:38 2018/6/24
         *
         * @Param: userId
         * @Param: shipping
         * @return com.mmall.common.ServerResponse<java.util.Map<java.lang.String,java.lang.Integer>>
         */
        if (userId ==null||shipping==null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        shipping.setUserId(userId);
        int resultCount=shippingMapper.insert(shipping);
        if (resultCount > 0) {
            Map<String,Integer> map= Maps.newHashMap();
            map.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",map);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    public ServerResponse deleteShipping(Integer userId,Integer shippingId){
        /**
         * create by: vain
         * description: 删除地址
         * create time: 下午12:45 2018/6/24
         *
         * @Param: userId
         * @Param: shippingId
         * @return com.mmall.common.ServerResponse
         */
        if (userId ==null||shippingId==null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int resultCount=shippingMapper.deleteByShippingIdUserId(userId,shippingId);
        if (resultCount>0){
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    public ServerResponse updateShipping(Integer userId,Shipping shipping){
        /**
         * create by: vain
         * description: 登录状态更新收货地址
         * create time: 下午12:47 2018/6/24
         *
         * @Param: userId
         * @Param: shipping
         * @return com.mmall.common.ServerResponse
         */
        if (userId ==null||shipping==null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        shipping.setUserId(userId);
        int resultCount=shippingMapper.updateByShipping(shipping);
        if (resultCount>0){
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    public ServerResponse<Shipping> selectShipping(Integer userId,Integer shippingId){
        /**
         * create by: vain
         * description: 选中查看具体的地址
         * create time: 下午12:59 2018/6/24
         *
         * @Param: userId
         * @Param: shippingId
         * @return com.mmall.common.ServerResponse<com.mmall.pojo.Shipping>
         */
        if (userId ==null||shippingId==null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping shipping =shippingMapper.selectByUserIdShippingId(userId,shippingId);
        if (shipping!=null){
            return ServerResponse.createBySuccess(shipping);
        }
        return ServerResponse.createByErrorMessage("请登录后查看");
    }

    public ServerResponse<PageInfo> listShipping(Integer userId,Integer pageNum,Integer pageSize){
        /**
         * create by: vain
         * description: 地址列表，带分页
         * create time: 下午1:12 2018/6/24
         *
         * @Param: userId
         * @return com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         */
        if (userId ==null||pageNum==null||pageSize==null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList= shippingMapper.selectListShippingByUserId(userId);
        PageInfo pageInfo=new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
