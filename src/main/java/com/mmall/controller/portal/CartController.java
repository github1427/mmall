package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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
    public ServerResponse<CartVo> list(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.cartList(user.getId());
    }

    @RequestMapping("/add")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session,Integer productId,Integer count){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.addProductToCart(user.getId(),productId,count);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session,Integer productId,Integer count){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.updateProductCount(user.getId(),productId,count);
    }

    @RequestMapping("/delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session,String productIds){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    @RequestMapping("/select")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpSession session,Integer productId){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectProduct(user.getId(),productId,Const.Cart.PRODUCT_CHECKED);
    }

    @RequestMapping("/un_select")
    @ResponseBody
    public ServerResponse<CartVo> un_select(HttpSession session,Integer productId){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectProduct(user.getId(),productId,Const.Cart.PRODUCT_UN_CHECKED);
    }

    @RequestMapping("/select_all")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectProduct(user.getId(),null,Const.Cart.PRODUCT_CHECKED);
    }

    @RequestMapping("/un_select_all")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelectProduct(user.getId(),null,Const.Cart.PRODUCT_UN_CHECKED);
    }

    @RequestMapping("/get_cart_product_count")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
