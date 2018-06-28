package com.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午3:40 2018/6/19
 * @ Description：
 */

@Service("iProductService")
public class ProductServiceImpl implements IProductService{
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse productInsertOrUpdate(Product product){
        /**
         * create by: vain
         * description: 新增OR更新商品
         * create time: 下午4:06 2018/6/19
         *
         * @Param: product
         * @return com.mmall.common.ServerResponse
         */
        if (product!=null){
            if (StringUtils.isNotBlank(product.getSubImages())){
                String[] images=product.getSubImages().split(",");
                product.setMainImage(images[0]);
            }
            if (product.getId()==null){
                int count=productMapper.insert(product);
                if (count>0){
                    return ServerResponse.createBySuccess("新增产品成功");
                }else {
                    return ServerResponse.createByErrorData("新增产品失败");
                }
            }else {
                int count=productMapper.updateByPrimaryKeySelective(product);
                if (count>0){
                    return ServerResponse.createBySuccess("更新产品成功");
                }else {
                    return ServerResponse.createByErrorData("更新产品失败");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("插入或更新参数错误");
        }
    }

    public ServerResponse setSaleStatus(Integer productId,Integer status){
        /**
         * create by: vain
         * description: 产品上下架（修改产品状态）
         * create time: 下午4:23 2018/6/19
         *
         * @Param: productId
         * @Param: status
         * @return com.mmall.common.ServerResponse
         */
        if (productId==null||status==null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int count=productMapper.updateByPrimaryKeySelective(product);
        if (count>0){
            return ServerResponse.createBySuccess("修改产品状态成功");
        }else {
            return ServerResponse.createByErrorData("修改产品状态失败");
        }
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        /**
         * create by: vain
         * description: 获得商品详细信息
         * create time: 上午10:30 2018/6/20
         *
         * @Param: productId
         * @return com.mmall.common.ServerResponse
         */
        if (productId==null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("商品已下架或删除");
        }
        ProductDetailVo productDetailVo=assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        /**
         * create by: vain
         * description: 对product进行扩展成productDetailVo
         * create time: 上午10:30 2018/6/20
         *
         * @Param: product
         * @return com.mmall.vo.ProductDetailVo
         */
        ProductDetailVo productDetailVo=new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(product.getName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setDetail(productDetailVo.getDetail());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
        productDetailVo.setParentCategoryId(category.getParentId());
        productDetailVo.setCreateTime(DateTimeUtil.dateToString(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToString(product.getUpdateTime()));
        return productDetailVo;

    }

    public ServerResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize){
        /**
         * create by: vain
         * description: 查询商品信息，并进行分页
         * create time: 下午12:56 2018/6/20
         *
         * @Param: pageNum
         * @Param: pageSize
         * @return com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         */
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList= productMapper.selectProductList();
        List<ProductListVo> productListVoList=Lists.newArrayList();
        for (Product product:productList){
            ProductListVo productListVo=assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);

    }

    private ProductListVo assembleProductListVo(Product product){
        /**
         * create by: vain
         * description: 对product进行属性选择以及扩展
         * create time: 下午12:57 2018/6/20
         *
         * @Param: product
         * @return com.mmall.vo.ProductListVo
         */
        ProductListVo productListVo=new ProductListVo();
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setStatus(product.getStatus());
        productListVo.setPrice(product.getPrice());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","ftp://10.211.55.12/img/"));
        return productListVo;
    }

    public ServerResponse<PageInfo> productSearch(String productName,Integer productId,Integer pageNum,Integer pageSize){
        /**
         * create by: vain
         * description: 产品搜索
         * create time: 下午1:58 2018/6/20
         *
         * @Param: productName
         * @Param: productId
         * @Param: pageNum
         * @Param: pageSize
         * @return com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         */
        if (StringUtils.isNotBlank(productName)){
            StringBuilder stringBuilder=new StringBuilder().append("%").append(productName).append("%");
            productName=stringBuilder.toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList=productMapper.selectProductByNameAndId(productName,productId);
        List<ProductListVo> productListVoList=Lists.newArrayList();
        for (Product product:productList){
            ProductListVo productListVo=assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);

    }

    public ServerResponse<ProductDetailVo> portalProductDetail(Integer productId){
        /**
         * create by: vain
         * description: 前台获取产品详细信息
         * create time: 上午9:43 2018/6/21
         *
         * @Param: productId
         * @return com.mmall.common.ServerResponse<com.mmall.pojo.Product>
         */
        if (productId==null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("商品已下架或删除");
        }
        if (product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("该商品已下架或删除");
        }
        return ServerResponse.createBySuccess(assembleProductDetailVo(product));
    }

    public ServerResponse<PageInfo> portalProductList(Integer categoryId,String keyword,Integer pageNum,Integer pageSize,String orderBy){
        /**
         * create by: vain
         * description: 产品搜索及动态排序list
         * create time: 上午10:28 2018/6/21
         *
         * @Param: categoryId
         * @Param: keyword
         * @Param: pageNum
         * @Param: pageSize
         * @Param: orderBy
         * @return com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         */
        if (categoryId==null&&StringUtils.isBlank(keyword)){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        List<Integer> categoryIds=iCategoryService.getDeepCategory(categoryId).getData();
        if (category==null&&StringUtils.isBlank(keyword)){
            PageHelper.startPage(pageNum,pageSize);
            List<ProductListVo> productListVoList=Lists.newArrayList();
            PageInfo pageInfo=new PageInfo(productListVoList);
            return ServerResponse.createBySuccess(pageInfo);
        }
        if (StringUtils.isNotBlank(keyword)){
            keyword= "%" + keyword + "%";
        }
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(orderBy)){
            if (Const.orderBy.PRODUCT_ASC_DESC.contains(orderBy)){
                String[] orderByArray=orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList=productMapper.selectProductByKeywordAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIds.size()==0?null:categoryIds);
        List<ProductListVo> productListVoList=Lists.newArrayList();
        for (Product product:productList){
            productListVoList.add(assembleProductListVo(product));
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
