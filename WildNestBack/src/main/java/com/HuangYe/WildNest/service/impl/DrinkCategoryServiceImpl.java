package com.HuangYe.WildNest.service.impl;

import com.HuangYe.WildNest.entity.DrinkCategory;
import com.HuangYe.WildNest.mapper.DrinkCategoryMapper;
import com.HuangYe.WildNest.service.DrinkCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 酒品分类服务实现类
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@Service
public class DrinkCategoryServiceImpl extends ServiceImpl<DrinkCategoryMapper, DrinkCategory> 
        implements DrinkCategoryService {

    @Override
    public List<DrinkCategory> getAllEnabledCategories() {
        try {
            return baseMapper.selectEnabledCategories();
        } catch (Exception e) {
            log.error("获取启用分类失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<DrinkCategory> getTopLevelCategories() {
        try {
            return baseMapper.selectTopLevelCategories();
        } catch (Exception e) {
            log.error("获取顶级分类失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<DrinkCategory> getCategoriesByParentId(Long parentId) {
        if (parentId == null) {
            return new ArrayList<>();
        }
        try {
            return baseMapper.selectByParentId(parentId);
        } catch (Exception e) {
            log.error("根据父分类ID获取子分类失败，parentId: {}", parentId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<DrinkCategory> searchCategoriesByName(String name) {
        if (!StringUtils.hasText(name)) {
            return new ArrayList<>();
        }
        try {
            return baseMapper.selectByNameLike(name.trim());
        } catch (Exception e) {
            log.error("根据名称搜索分类失败，name: {}", name, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<DrinkCategory> getCategoryTree() {
        try {
            // 获取所有启用的分类
            List<DrinkCategory> allCategories = getAllEnabledCategories();
            
            // 构建分类树
            return buildCategoryTree(allCategories, 0L);
        } catch (Exception e) {
            log.error("构建分类树失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 递归构建分类树
     * 
     * @param allCategories 所有分类
     * @param parentId 父分类ID
     * @return 分类树
     */
    private List<DrinkCategory> buildCategoryTree(List<DrinkCategory> allCategories, Long parentId) {
        Map<Long, List<DrinkCategory>> categoryMap = allCategories.stream()
                .collect(Collectors.groupingBy(DrinkCategory::getParentId));
        
        return categoryMap.getOrDefault(parentId, new ArrayList<>())
                .stream()
                .peek(category -> {
                    // 递归设置子分类
                    List<DrinkCategory> children = buildCategoryTree(allCategories, category.getId());
                    // 这里可以添加children字段到DrinkCategory实体中，或者使用DTO
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createCategory(DrinkCategory category) {
        if (category == null || !StringUtils.hasText(category.getName())) {
            log.warn("创建分类失败，参数无效");
            return false;
        }
        
        try {
            // 设置默认值
            if (category.getParentId() == null) {
                category.setParentId(0L);
            }
            if (category.getSortOrder() == null) {
                category.setSortOrder(0);
            }
            if (category.getStatus() == null) {
                category.setStatus(1);
            }
            
            boolean result = save(category);
            log.info("创建分类成功，分类名称: {}, ID: {}", category.getName(), category.getId());
            return result;
        } catch (Exception e) {
            log.error("创建分类失败，分类名称: {}", category.getName(), e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(DrinkCategory category) {
        if (category == null || category.getId() == null) {
            log.warn("更新分类失败，参数无效");
            return false;
        }
        
        try {
            boolean result = updateById(category);
            log.info("更新分类成功，分类ID: {}", category.getId());
            return result;
        } catch (Exception e) {
            log.error("更新分类失败，分类ID: {}", category.getId(), e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        if (id == null) {
            log.warn("删除分类失败，ID为空");
            return false;
        }
        
        try {
            // 检查是否有子分类
            List<DrinkCategory> children = getCategoriesByParentId(id);
            if (!children.isEmpty()) {
                log.warn("删除分类失败，存在子分类，分类ID: {}", id);
                return false;
            }
            
            // 软删除：设置状态为0
            boolean result = updateCategoryStatus(id, 0);
            log.info("删除分类成功，分类ID: {}", id);
            return result;
        } catch (Exception e) {
            log.error("删除分类失败，分类ID: {}", id, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategoryStatus(Long id, Integer status) {
        if (id == null || status == null) {
            log.warn("更新分类状态失败，参数无效");
            return false;
        }
        
        try {
            LambdaUpdateWrapper<DrinkCategory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(DrinkCategory::getId, id)
                    .set(DrinkCategory::getStatus, status);
            
            boolean result = update(updateWrapper);
            log.info("更新分类状态成功，分类ID: {}, 状态: {}", id, status);
            return result;
        } catch (Exception e) {
            log.error("更新分类状态失败，分类ID: {}, 状态: {}", id, status, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategorySortOrder(Long id, Integer sortOrder) {
        if (id == null || sortOrder == null) {
            log.warn("更新分类排序失败，参数无效");
            return false;
        }
        
        try {
            LambdaUpdateWrapper<DrinkCategory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(DrinkCategory::getId, id)
                    .set(DrinkCategory::getSortOrder, sortOrder);
            
            boolean result = update(updateWrapper);
            log.info("更新分类排序成功，分类ID: {}, 排序值: {}", id, sortOrder);
            return result;
        } catch (Exception e) {
            log.error("更新分类排序失败，分类ID: {}, 排序值: {}", id, sortOrder, e);
            throw e;
        }
    }
}