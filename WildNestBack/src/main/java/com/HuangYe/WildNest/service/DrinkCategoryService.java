package com.HuangYe.WildNest.service;

import com.HuangYe.WildNest.entity.DrinkCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 酒品分类服务接口
 * 
 * @author HuangYe
 * @since 2024
 */
public interface DrinkCategoryService extends IService<DrinkCategory> {

    /**
     * 获取所有启用的分类
     * 
     * @return 分类列表
     */
    List<DrinkCategory> getAllEnabledCategories();

    /**
     * 获取顶级分类
     * 
     * @return 顶级分类列表
     */
    List<DrinkCategory> getTopLevelCategories();

    /**
     * 根据父分类ID获取子分类
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<DrinkCategory> getCategoriesByParentId(Long parentId);

    /**
     * 根据分类名称搜索
     * 
     * @param name 分类名称
     * @return 分类列表
     */
    List<DrinkCategory> searchCategoriesByName(String name);

    /**
     * 获取分类树结构
     * 
     * @return 分类树
     */
    List<DrinkCategory> getCategoryTree();

    /**
     * 创建分类
     * 
     * @param category 分类信息
     * @return 创建结果
     */
    boolean createCategory(DrinkCategory category);

    /**
     * 更新分类
     * 
     * @param category 分类信息
     * @return 更新结果
     */
    boolean updateCategory(DrinkCategory category);

    /**
     * 删除分类（软删除）
     * 
     * @param id 分类ID
     * @return 删除结果
     */
    boolean deleteCategory(Long id);

    /**
     * 启用/禁用分类
     * 
     * @param id 分类ID
     * @param status 状态（1-启用，0-禁用）
     * @return 操作结果
     */
    boolean updateCategoryStatus(Long id, Integer status);

    /**
     * 调整分类排序
     * 
     * @param id 分类ID
     * @param sortOrder 排序值
     * @return 操作结果
     */
    boolean updateCategorySortOrder(Long id, Integer sortOrder);
}