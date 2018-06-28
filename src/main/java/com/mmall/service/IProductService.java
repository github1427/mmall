package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午3:40 2018/6/19
 * @ Description：
 */
public interface IProductService {
    ServerResponse productInsertOrUpdate(Product product);

    ServerResponse setSaleStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> productSearch(String productName,Integer productId,Integer pageNum,Integer pageSize);

    ServerResponse<ProductDetailVo> portalProductDetail(Integer productId);

    ServerResponse<PageInfo> portalProductList(Integer categoryId,String keyword,Integer pageNum,Integer pageSize,String orderBy);
}
