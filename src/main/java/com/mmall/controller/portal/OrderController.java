package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import com.mmall.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午1:49 2018/6/26
 * @ Description：订单处理器
 */

@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController {


    @Autowired
    IOrderService iOrderService;

    @RequestMapping("/pay.do")
    @ResponseBody
    public ServerResponse<Map<String,String>> pay(HttpServletRequest httpServletRequest, Long orderNo, HttpServletRequest request){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String path=request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.payOrder(orderNo,user.getId(),path);
    }

    @RequestMapping("/alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params= Maps.newHashMap();
        Map requestParams=request.getParameterMap();
        for (Object o : requestParams.keySet()) {
            String name = (String) o;
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        log.info("支付宝回调：sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
        params.remove("sign_type");
        try {
            boolean alipayRSACheckV2= AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if (!alipayRSACheckV2){
                return ServerResponse.createByErrorMessage("验证失败，非法请求,如再次尝试将联系网警");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝回调异常",e);
        }
        ServerResponse serverResponse=iOrderService.checkAliCallback(params);
        if (serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    @RequestMapping("/query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpServletRequest httpServletRequest,Long orderNo){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.queryOrderPayStatus(user.getId(),orderNo);
    }

    @RequestMapping("/create.do")
    @ResponseBody
    public ServerResponse createOrder(HttpServletRequest httpServletRequest,Integer shippingId){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    @RequestMapping("/cancel.do")
    @ResponseBody
    public ServerResponse cancelOrder(HttpServletRequest httpServletRequest,Long orderNo){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancelOrder(user.getId(),orderNo);
    }

    @RequestMapping("/get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpServletRequest httpServletRequest){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpServletRequest httpServletRequest,
                                              @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.orderList(user.getId(),pageNum,pageSize);
    }

    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpServletRequest httpServletRequest, Long orderNo){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.orderDetail(user.getId(),orderNo);
    }


}
