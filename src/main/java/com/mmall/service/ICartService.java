package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午1:36 2018/6/22
 * @ Description：购物车功能接口
 */
public interface ICartService {
    ServerResponse<CartVo> addProductToCart(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> updateProductCount(Integer userId,Integer productId,Integer count);

    ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);

    ServerResponse<CartVo> cartList(Integer userId);

    ServerResponse<CartVo> selectOrUnSelectProduct(Integer userId,Integer productId,Integer checked);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
