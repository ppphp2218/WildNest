package com.HuangYe.WildNest.mapper;

import com.HuangYe.WildNest.entity.Drink;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 酒品Mapper接口
 * 
 * @author HuangYe
 * @since 2024
 */
@Mapper
public interface DrinkMapper extends BaseMapper<Drink> {

    /**
     * 分页查询可售酒品
     * 
     * @param page 分页参数
     * @param categoryId 分类ID（可选）
     * @return 酒品分页数据
     */
    @Select("<script>" +
            "SELECT * FROM drink WHERE is_available = 1 " +
            "<if test='categoryId != null'> AND category_id = #{categoryId} </if>" +
            "ORDER BY is_featured DESC, sort_order ASC, created_at DESC" +
            "</script>")
    IPage<Drink> selectAvailableDrinks(Page<Drink> page, @Param("categoryId") Long categoryId);

    /**
     * 根据关键词搜索酒品
     * 
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @return 酒品分页数据
     */
    @Select("SELECT * FROM drink WHERE is_available = 1 " +
            "AND (name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR english_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR description LIKE CONCAT('%', #{keyword}, '%') " +
            "OR tags LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY is_featured DESC, view_count DESC, created_at DESC")
    IPage<Drink> searchDrinks(Page<Drink> page, @Param("keyword") String keyword);

    /**
     * 查询推荐酒品（is_featured = 1）
     * 
     * @param limit 限制数量
     * @return 推荐酒品列表
     */
    @Select("SELECT * FROM drink WHERE is_available = 1 AND is_featured = 1 " +
            "ORDER BY sort_order ASC, view_count DESC LIMIT #{limit}")
    List<Drink> selectFeaturedDrinks(@Param("limit") Integer limit);

    /**
     * 根据分类ID查询酒品数量
     * 
     * @param categoryId 分类ID
     * @return 酒品数量
     */
    @Select("SELECT COUNT(*) FROM drink WHERE category_id = #{categoryId} AND is_available = 1")
    Integer countByCategoryId(Long categoryId);

    /**
     * 增加浏览次数
     * 
     * @param id 酒品ID
     * @return 更新行数
     */
    @Update("UPDATE drink SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);

    /**
     * 根据标签查询酒品
     * 
     * @param page 分页参数
     * @param tag 标签
     * @return 酒品分页数据
     */
    @Select("SELECT * FROM drink WHERE is_available = 1 " +
            "AND tags LIKE CONCAT('%', #{tag}, '%') " +
            "ORDER BY is_featured DESC, view_count DESC")
    IPage<Drink> selectByTag(Page<Drink> page, @Param("tag") String tag);

    /**
     * 根据价格区间查询酒品
     * 
     * @param page 分页参数
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 酒品分页数据
     */
    @Select("SELECT * FROM drink WHERE is_available = 1 " +
            "AND price >= #{minPrice} AND price <= #{maxPrice} " +
            "ORDER BY price ASC")
    IPage<Drink> selectByPriceRange(Page<Drink> page, 
                                   @Param("minPrice") Double minPrice, 
                                   @Param("maxPrice") Double maxPrice);

    /**
     * 查询热门酒品（按浏览量排序）
     * 
     * @param limit 限制数量
     * @return 热门酒品列表
     */
    @Select("SELECT * FROM drink WHERE is_available = 1 " +
            "ORDER BY view_count DESC, created_at DESC LIMIT #{limit}")
    List<Drink> selectPopularDrinks(@Param("limit") Integer limit);
}