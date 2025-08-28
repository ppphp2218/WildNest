package com.HuangYe.WildNest.mapper;

import com.HuangYe.WildNest.entity.DrinkCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 酒品分类Mapper接口
 * 
 * @author HuangYe
 * @since 2024
 */
@Mapper
public interface DrinkCategoryMapper extends BaseMapper<DrinkCategory> {

    /**
     * 查询所有启用的分类，按排序值排序
     * 
     * @return 分类列表
     */
    @Select("SELECT * FROM drink_category WHERE status = 1 ORDER BY sort_order ASC, created_at ASC")
    List<DrinkCategory> selectEnabledCategories();

    /**
     * 根据父分类ID查询子分类
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM drink_category WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort_order ASC")
    List<DrinkCategory> selectByParentId(Long parentId);

    /**
     * 查询顶级分类（父分类ID为0）
     * 
     * @return 顶级分类列表
     */
    @Select("SELECT * FROM drink_category WHERE parent_id = 0 AND status = 1 ORDER BY sort_order ASC")
    List<DrinkCategory> selectTopLevelCategories();

    /**
     * 根据分类名称查询（模糊匹配）
     * 
     * @param name 分类名称
     * @return 分类列表
     */
    @Select("SELECT * FROM drink_category WHERE name LIKE CONCAT('%', #{name}, '%') AND status = 1")
    List<DrinkCategory> selectByNameLike(String name);
}