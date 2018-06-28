package com.mmall.vo;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午1:21 2018/6/22
 * @ Description：对CartProductVo类的一个扩展
 */
public class CartVo {
    private List<CartProductVo> cartProductVoList= Lists.newArrayList();
    private boolean allChecked;
    private BigDecimal cartTotalPrice;
    private String imageHost;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public boolean isAllChecked() {
        return allChecked;
    }

    public void setAllChecked(boolean allChecked) {
        this.allChecked = allChecked;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
