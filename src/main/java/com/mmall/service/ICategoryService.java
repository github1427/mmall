package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 上午9:48 2018/6/19
 * @ Description：分类模块功能接口
 */
public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);
    ServerResponse setCategoryName(Integer categoryId,String categoryName);
    ServerResponse<List<Category>> getCategoryAndParallelChildrenCategory(Integer categoryId);
    ServerResponse<List<Integer>> getDeepCategory(Integer categoryId);
}
