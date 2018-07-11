package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.CartVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午1:37 2018/6/22
 * @ Description：前台购物车处理器
 */

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("/list")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpServletRequest httpServletRequest){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.cartList(user.getId());
    }

    @RequestMapping("/add")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpServletRequest httpServletRequest,Integer productId,Integer count){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.addProductToCart(user.getId(),productId,count);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpServletRequest httpServletRequest,Integer productId,Integer count){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.updateProductCount(user.getId(),productId,count);
    }

    @RequestMapping("/delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpServletRequest httpServletRequest,String productIds){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    @RequestMapping("/select")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpServletRequest httpServletRequest,Integer productId){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectProduct(user.getId(),productId,Const.Cart.PRODUCT_CHECKED);
    }

    @RequestMapping("/un_select")
    @ResponseBody
    public ServerResponse<CartVo> un_select(HttpServletRequest httpServletRequest,Integer productId){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectProduct(user.getId(),productId,Const.Cart.PRODUCT_UN_CHECKED);
    }

    @RequestMapping("/select_all")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpServletRequest httpServletRequest){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectProduct(user.getId(),null,Const.Cart.PRODUCT_CHECKED);
    }

    @RequestMapping("/un_select_all")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpServletRequest httpServletRequest){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectProduct(user.getId(),null,Const.Cart.PRODUCT_UN_CHECKED);
    }

    @RequestMapping("/get_cart_product_count")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest httpServletRequest){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(loginUserJson,User.class);
        if (user==null){
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
