package com.mmall.service.Impl;

import com.google.common.base.Splitter;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午1:37 2018/6/22
 * @ Description：购物车功能实现
 */

@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVo> cartList(Integer userId){
        /**
         * create by: vain
         * description: 购物车列表
         * create time: 下午6:09 2018/6/22
         *
         * @Param: userId
         * @return com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         */
        CartVo cartVo=getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> addProductToCart(Integer userId, Integer productId, Integer count) {
        /**
         * create by: vain
         * description: 向购物车中添加商品
         * create time: 下午4:29 2018/6/22
         *
         * @Param: userId
         * @Param: productId
         * @Param: count
         * @return com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         */
        if (productId == null || count <= 0) {
            ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null || product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("商品不存在或已下架");
        }
        Cart cart = cartMapper.selectCartItemListByUserIdProductId(userId, productId);
        if (cart == null) {
            Cart cartItem = new Cart();
            cartItem.setUserId(userId);
            cartItem.setProductId(product.getId());
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.ProductStatusEnum.ON_SALE.getCode());
            int resultInsert = cartMapper.insert(cartItem);
            if (resultInsert <= 0) {
                return ServerResponse.createByErrorMessage("添加购物车失败");
            }
        } else {
            count = count + cart.getQuantity();
            Cart cartItem = new Cart();
            cartItem.setId(cart.getId());
            cartItem.setQuantity(count);
            int result = cartMapper.updateByPrimaryKeySelective(cartItem);
            if (result <= 0) {
                return ServerResponse.createByErrorMessage("添加购物车失败");
            }
        }
       return cartList(userId);
    }

    private CartVo getCartVoLimit(Integer userId) {
        /**
         * create by: vain
         * description: 对超出库存数量的商品采取措施，并进行CartVo的封装
         * create time: 下午4:29 2018/6/22
         *
         * @Param: userId
         * @return com.mmall.vo.CartVo
         */
        CartVo cartVo = new CartVo();
        List<CartProductVo> cartProductVoList = cartMapper.selectCartItemWithProductByUserId(userId);
        BigDecimal cartTotalPrice = new BigDecimal("0");
        int buyLimitCount = 0;
        for (CartProductVo cartProductVo : cartProductVoList) {
            if (cartProductVo.getQuantity() > cartProductVo.getProductStock()) {
                buyLimitCount = cartProductVo.getProductStock();
                cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                Cart cart = new Cart();
                cart.setId(cartProductVo.getId());
                cart.setQuantity(buyLimitCount);
                int result = cartMapper.updateByPrimaryKeySelective(cart);
                if (result <= 0) {
                    return null;
                }
            } else {
                buyLimitCount = cartProductVo.getQuantity();
                cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
            }
            cartProductVo.setQuantity(buyLimitCount);
            BigDecimal productTotalPrice = BigDecimalUtil.mul(buyLimitCount, cartProductVo.getProductPrice().doubleValue());
            cartProductVo.setProductTotalPrice(productTotalPrice);
            if (cartProductVo.getProductChecked()==Const.Cart.PRODUCT_CHECKED){
                cartTotalPrice = cartTotalPrice.add(productTotalPrice);
            }
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(judgeAllChecked(cartProductVoList));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "ftp://10.211.55.12/img/"));
        return cartVo;
    }

    private boolean judgeAllChecked(List<CartProductVo> cartProductVoList) {
        /**
         * create by: vain
         * description: 判断购物车中的商品是否都选中
         * create time: 下午4:30 2018/6/22
         *
         * @Param: cartProductVoList
         * @return boolean
         */
        boolean flag = true;
        for (CartProductVo cartProductVo : cartProductVoList) {
            if (cartProductVo.getProductChecked() == Const.Cart.PRODUCT_UN_CHECKED) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public ServerResponse<CartVo> updateProductCount(Integer userId,Integer productId,Integer count){
        /**
         * create by: vain
         * description: 改变购物车中商品的数量
         * create time: 下午4:38 2018/6/22
         *
         * @Param: userId
         * @Param: productId
         * @Param: count
         * @return com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         */
        if (productId == null || count <= 0) {
            ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null || product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("商品不存在或已下架");
        }
        Cart cart = cartMapper.selectCartItemListByUserIdProductId(userId, productId);
        cart.setQuantity(count);
        cartMapper.updateByPrimaryKeySelective(cart);
        return cartList(userId);
    }

    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        /**
         * create by: vain
         * description: 移除购物车中的商品
         * create time: 下午4:51 2018/6/22
         *
         * @Param: userId
         * @Param: productIds
         * @return com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         */
        List<String> deleteProductList= Splitter.on(",").splitToList(productIds);
        int result=cartMapper.deleteProductByUserIdAndProductIds(userId,deleteProductList);
        if (result<=0){
            return ServerResponse.createByErrorMessage("删除商品失败");
        }
        return cartList(userId);
    }

    public ServerResponse<CartVo> selectOrUnSelectProduct(Integer userId,Integer productId,Integer checked){
        /**
         * create by: vain
         * description: 全选，全不选，单选 ，单不选
         * create time: 下午6:15 2018/6/22
         *
         * @Param: userId
         * @Param: checked
         * @return com.mmall.common.ServerResponse<com.mmall.vo.CartVo>
         */
        if (userId==null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int result=cartMapper.checkedOrUnChecked(userId,productId,checked);
        if (result<=0){
            return ServerResponse.createByErrorMessage("操作失败");
        }
        return cartList(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId){
        /**
         * create by: vain
         * description: 获得购物车中商品的数量
         * create time: 下午6:47 2018/6/22
         *
         * @Param: userId
         * @return com.mmall.common.ServerResponse<java.lang.Integer>
         */
        if (userId==null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.getCartProductCount(userId));
    }
}
