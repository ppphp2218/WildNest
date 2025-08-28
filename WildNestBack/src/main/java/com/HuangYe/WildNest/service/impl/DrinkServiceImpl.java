package com.HuangYe.WildNest.service.impl;

import com.HuangYe.WildNest.entity.Drink;
import com.HuangYe.WildNest.mapper.DrinkMapper;
import com.HuangYe.WildNest.service.DrinkService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 酒品服务实现类
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@Service
public class DrinkServiceImpl extends ServiceImpl<DrinkMapper, Drink> implements DrinkService {

    @Override
    public IPage<Drink> getAvailableDrinks(Long current, Long size, Long categoryId) {
        try {
            Page<Drink> page = new Page<>(current, size);
            return baseMapper.selectAvailableDrinks(page, categoryId);
        } catch (Exception e) {
            log.error("分页查询可售酒品失败，current: {}, size: {}, categoryId: {}", current, size, categoryId, e);
            return new Page<>(current, size);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Drink getDrinkDetailById(Long id) {
        if (id == null) {
            log.warn("获取酒品详情失败，ID为空");
            return null;
        }
        
        try {
            // 查询酒品详情
            Drink drink = getById(id);
            if (drink == null || !drink.getIsAvailable()) {
                log.warn("酒品不存在或已下架，ID: {}", id);
                return null;
            }
            
            // 增加浏览量
            baseMapper.incrementViewCount(id);
            
            log.info("获取酒品详情成功，ID: {}, 名称: {}", id, drink.getName());
            return drink;
        } catch (Exception e) {
            log.error("获取酒品详情失败，ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public IPage<Drink> searchDrinks(Long current, Long size, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return new Page<>(current, size);
        }
        
        try {
            Page<Drink> page = new Page<>(current, size);
            return baseMapper.searchDrinks(page, keyword.trim());
        } catch (Exception e) {
            log.error("搜索酒品失败，keyword: {}", keyword, e);
            return new Page<>(current, size);
        }
    }

    @Override
    public List<Drink> getFeaturedDrinks(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认10条
        }
        
        try {
            return baseMapper.selectFeaturedDrinks(limit);
        } catch (Exception e) {
            log.error("获取推荐酒品失败，limit: {}", limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Drink> getPopularDrinks(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认10条
        }
        
        try {
            return baseMapper.selectPopularDrinks(limit);
        } catch (Exception e) {
            log.error("获取热门酒品失败，limit: {}", limit, e);
            return new ArrayList<>();
        }
    }

    @Override
    public IPage<Drink> getDrinksByTag(Long current, Long size, String tag) {
        if (!StringUtils.hasText(tag)) {
            return new Page<>(current, size);
        }
        
        try {
            Page<Drink> page = new Page<>(current, size);
            return baseMapper.selectByTag(page, tag.trim());
        } catch (Exception e) {
            log.error("根据标签查询酒品失败，tag: {}", tag, e);
            return new Page<>(current, size);
        }
    }

    @Override
    public IPage<Drink> getDrinksByPriceRange(Long current, Long size, Double minPrice, Double maxPrice) {
        if (minPrice == null || maxPrice == null || minPrice < 0 || maxPrice < minPrice) {
            log.warn("价格区间参数无效，minPrice: {}, maxPrice: {}", minPrice, maxPrice);
            return new Page<>(current, size);
        }
        
        try {
            Page<Drink> page = new Page<>(current, size);
            return baseMapper.selectByPriceRange(page, minPrice, maxPrice);
        } catch (Exception e) {
            log.error("根据价格区间查询酒品失败，minPrice: {}, maxPrice: {}", minPrice, maxPrice, e);
            return new Page<>(current, size);
        }
    }

    @Override
    public Integer getDrinkCountByCategory(Long categoryId) {
        if (categoryId == null) {
            return 0;
        }
        
        try {
            return baseMapper.countByCategoryId(categoryId);
        } catch (Exception e) {
            log.error("获取分类酒品数量失败，categoryId: {}", categoryId, e);
            return 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDrink(Drink drink) {
        if (drink == null || !StringUtils.hasText(drink.getName())) {
            log.warn("创建酒品失败，参数无效");
            return false;
        }
        
        try {
            // 设置默认值
            if (drink.getPrice() == null) {
                drink.setPrice(BigDecimal.ZERO);
            }
            if (drink.getIsFeatured() == null) {
                drink.setIsFeatured(false);
            }
            if (drink.getIsAvailable() == null) {
                drink.setIsAvailable(true);
            }
            if (drink.getViewCount() == null) {
                drink.setViewCount(0);
            }
            if (drink.getSortOrder() == null) {
                drink.setSortOrder(0);
            }
            
            boolean result = save(drink);
            log.info("创建酒品成功，酒品名称: {}, ID: {}", drink.getName(), drink.getId());
            return result;
        } catch (Exception e) {
            log.error("创建酒品失败，酒品名称: {}", drink.getName(), e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDrink(Drink drink) {
        if (drink == null || drink.getId() == null) {
            log.warn("更新酒品失败，参数无效");
            return false;
        }
        
        try {
            boolean result = updateById(drink);
            log.info("更新酒品成功，酒品ID: {}", drink.getId());
            return result;
        } catch (Exception e) {
            log.error("更新酒品失败，酒品ID: {}", drink.getId(), e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDrink(Long id) {
        if (id == null) {
            log.warn("删除酒品失败，ID为空");
            return false;
        }
        
        try {
            // 软删除：设置为不可售
            boolean result = updateDrinkAvailability(id, 0);
            log.info("删除酒品成功，酒品ID: {}", id);
            return result;
        } catch (Exception e) {
            log.error("删除酒品失败，酒品ID: {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDrinkAvailability(Long id, Integer isAvailable) {
        if (id == null || isAvailable == null) {
            log.warn("更新酒品可售状态失败，参数无效");
            return false;
        }
        
        try {
            LambdaUpdateWrapper<Drink> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Drink::getId, id)
                    .set(Drink::getIsAvailable, isAvailable);
            
            boolean result = update(updateWrapper);
            log.info("更新酒品可售状态成功，酒品ID: {}, 状态: {}", id, isAvailable);
            return result;
        } catch (Exception e) {
            log.error("更新酒品可售状态失败，酒品ID: {}, 状态: {}", id, isAvailable, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDrinkFeatured(Long id, Integer isFeatured) {
        if (id == null || isFeatured == null) {
            log.warn("更新酒品推荐状态失败，参数无效");
            return false;
        }
        
        try {
            LambdaUpdateWrapper<Drink> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Drink::getId, id)
                    .set(Drink::getIsFeatured, isFeatured);
            
            boolean result = update(updateWrapper);
            log.info("更新酒品推荐状态成功，酒品ID: {}, 状态: {}", id, isFeatured);
            return result;
        } catch (Exception e) {
            log.error("更新酒品推荐状态失败，酒品ID: {}, 状态: {}", id, isFeatured, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateSortOrder(List<Long> drinkIds, List<Integer> sortOrders) {
        if (drinkIds == null || sortOrders == null || drinkIds.size() != sortOrders.size()) {
            log.warn("批量更新酒品排序失败，参数无效");
            return false;
        }
        
        try {
            for (int i = 0; i < drinkIds.size(); i++) {
                LambdaUpdateWrapper<Drink> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Drink::getId, drinkIds.get(i))
                        .set(Drink::getSortOrder, sortOrders.get(i));
                update(updateWrapper);
            }
            
            log.info("批量更新酒品排序成功，更新数量: {}", drinkIds.size());
            return true;
        } catch (Exception e) {
            log.error("批量更新酒品排序失败", e);
            throw e;
        }
    }
}