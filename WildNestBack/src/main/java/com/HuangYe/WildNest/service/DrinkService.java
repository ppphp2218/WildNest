package com.HuangYe.WildNest.service;

import com.HuangYe.WildNest.entity.Drink;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 酒品服务接口
 * 
 * @author HuangYe
 * @since 2024
 */
public interface DrinkService extends IService<Drink> {

    /**
     * 分页查询可售酒品
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param categoryId 分类ID（可选）
     * @return 酒品分页数据
     */
    IPage<Drink> getAvailableDrinks(Long current, Long size, Long categoryId);

    /**
     * 根据ID获取酒品详情（会增加浏览量）
     * 
     * @param id 酒品ID
     * @return 酒品详情
     */
    Drink getDrinkDetailById(Long id);

    /**
     * 搜索酒品
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 酒品分页数据
     */
    IPage<Drink> searchDrinks(Long current, Long size, String keyword);

    /**
     * 获取推荐酒品
     * 
     * @param limit 限制数量
     * @return 推荐酒品列表
     */
    List<Drink> getFeaturedDrinks(Integer limit);

    /**
     * 获取热门酒品
     * 
     * @param limit 限制数量
     * @return 热门酒品列表
     */
    List<Drink> getPopularDrinks(Integer limit);

    /**
     * 根据标签查询酒品
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param tag 标签
     * @return 酒品分页数据
     */
    IPage<Drink> getDrinksByTag(Long current, Long size, String tag);

    /**
     * 根据价格区间查询酒品
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 酒品分页数据
     */
    IPage<Drink> getDrinksByPriceRange(Long current, Long size, Double minPrice, Double maxPrice);

    /**
     * 获取分类下的酒品数量
     * 
     * @param categoryId 分类ID
     * @return 酒品数量
     */
    Integer getDrinkCountByCategory(Long categoryId);

    /**
     * 创建酒品
     * 
     * @param drink 酒品信息
     * @return 创建结果
     */
    boolean createDrink(Drink drink);

    /**
     * 更新酒品
     * 
     * @param drink 酒品信息
     * @return 更新结果
     */
    boolean updateDrink(Drink drink);

    /**
     * 删除酒品（软删除）
     * 
     * @param id 酒品ID
     * @return 删除结果
     */
    boolean deleteDrink(Long id);

    /**
     * 上架/下架酒品
     * 
     * @param id 酒品ID
     * @param isAvailable 是否可售（1-可售，0-下架）
     * @return 操作结果
     */
    boolean updateDrinkAvailability(Long id, Integer isAvailable);

    /**
     * 设置/取消推荐酒品
     * 
     * @param id 酒品ID
     * @param isFeatured 是否推荐（1-推荐，0-取消推荐）
     * @return 操作结果
     */
    boolean updateDrinkFeatured(Long id, Integer isFeatured);

    /**
     * 批量更新酒品排序
     * 
     * @param drinkIds 酒品ID列表
     * @param sortOrders 对应的排序值列表
     * @return 操作结果
     */
    boolean batchUpdateSortOrder(List<Long> drinkIds, List<Integer> sortOrders);
}