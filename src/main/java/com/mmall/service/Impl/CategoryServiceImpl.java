package com.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 上午9:49 2018/6/19
 * @ Description：分类模块功能实现。
 */
@Service("iCategoryService")
@Slf4j
public class CategoryServiceImpl implements ICategoryService{

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName,Integer parentId){
        /**
         * create by: vain
         * description: 增加品类
         * create time: 上午10:22 2018/6/19
         *
         * @Param: categoryName
         * @Param: parentId
         * @return com.mmall.common.ServerResponse
         */
        if (parentId==null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int count=categoryMapper.insert(category);
        if (count>0){
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }else {
            return ServerResponse.createByErrorMessage("添加品类失败");
        }
    }

    public ServerResponse setCategoryName(Integer categoryId,String categoryName){
        /**
         * create by: vain
         * description: 修改品类名字
         * create time: 上午10:27 2018/6/19
         *
         * @Param: categoryId
         * @Param: categoryName
         * @return com.mmall.common.ServerResponse
         */
        Category category=new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int count=categoryMapper.updateByPrimaryKeySelective(category);
        if (count>0){
            return ServerResponse.createBySuccessMessage("更新品类名字成功");
        }else {
            return ServerResponse.createByErrorMessage("更新品类名字失败");
        }
    }

    public ServerResponse<List<Category>> getCategoryAndParallelChildrenCategory(Integer categoryId){
        /**
         * create by: vain
         * description: 获取当前分类id下的所有平行子分类
         * create time: 下午12:51 2018/6/19
         *
         * @Param: categoryId
         * @return com.mmall.common.ServerResponse<java.util.List<com.mmall.pojo.Category>>
         */
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if (category==null){
            return ServerResponse.createByErrorMessage("未找到该品类");
        }
        List<Category> categoryList=categoryMapper.selectChildrenCategoryByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            log.info("该分类下没有子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    public ServerResponse<List<Integer>> getDeepCategory(Integer categoryId){
        /**
         * create by: vain
         * description: 递归查询出本节点的id及其所有孩子节点id
         * create time: 下午1:29 2018/6/19
         *
         * @Param: categoryId
         * @return com.mmall.common.ServerResponse<java.util.List<java.lang.Integer>>
         */
        Set<Category> set=Sets.newHashSet();
        findChildrenCategory(set,categoryId);
        List<Integer> list= Lists.newArrayList();
        for (Category categoryItem:set){
            list.add(categoryItem.getId());
        }
        return ServerResponse.createBySuccess(list);
    }

    private void findChildrenCategory(Set<Category> categorySet,Integer categoryId){
        /**
         * create by: vain
         * description: 递归算法，查询出所有子节点
         * create time: 下午1:31 2018/6/19
         *
         * @Param: categorySet
         * @Param: categoryId
         * @return void
         */
        Category category=categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null){
            categorySet.add(category);
        }
        List<Category> categoryList= categoryMapper.selectChildrenCategoryByParentId(categoryId);
        for (Category categoryItem:categoryList){
            findChildrenCategory(categorySet,categoryItem.getId());
        }
    }

}
