package com.mmall.dao;

import com.mmall.pojo.Cart;
import com.mmall.vo.CartProductVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartItemListByUserIdProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<CartProductVo> selectCartItemWithProductByUserId(Integer userId);

    int deleteProductByUserIdAndProductIds(@Param("userId") Integer userId,@Param("deleteProductList")List<String> deleteProductList);

    int checkedOrUnChecked(@Param("userId")Integer userId,@Param("productId") Integer productId,@Param("checked") Integer checked);

    int getCartProductCount(Integer userId);

    List<Cart> selectCartCheckedByUserId(Integer userId);
}