package com.HuangYe.WildNest.service.impl;

import com.HuangYe.WildNest.entity.Option;
import com.HuangYe.WildNest.mapper.OptionMapper;
import com.HuangYe.WildNest.service.OptionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推荐选项服务实现类
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OptionServiceImpl extends ServiceImpl<OptionMapper, Option> implements OptionService {

    @Override
    public List<Option> getActiveOptionsByQuestionId(Long questionId) {
        if (questionId == null) {
            return List.of();
        }
        
        try {
            List<Option> options = baseMapper.selectActiveOptionsByQuestionId(questionId);
            log.info("获取问题启用选项成功，问题ID: {}, 数量: {}", questionId, options.size());
            return options;
        } catch (Exception e) {
            log.error("获取问题启用选项失败，问题ID: {}", questionId, e);
            return List.of();
        }
    }

    @Override
    public List<Option> getAllOptionsByQuestionId(Long questionId) {
        if (questionId == null) {
            return List.of();
        }
        
        try {
            List<Option> options = baseMapper.selectAllOptionsByQuestionId(questionId);
            log.info("获取问题所有选项成功，问题ID: {}, 数量: {}", questionId, options.size());
            return options;
        } catch (Exception e) {
            log.error("获取问题所有选项失败，问题ID: {}", questionId, e);
            return List.of();
        }
    }

    @Override
    public IPage<Option> getOptionsPage(Long current, Long size, Long questionId, Boolean isActive) {
        try {
            Page<Option> page = new Page<>(current, size);
            
            // 使用LambdaQueryWrapper构建动态查询条件
            LambdaQueryWrapper<Option> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(questionId != null, Option::getQuestionId, questionId)
                   .eq(isActive != null, Option::getIsActive, isActive)
                   .orderByAsc(Option::getQuestionId)
                   .orderByAsc(Option::getSortOrder)
                   .orderByDesc(Option::getCreatedAt);
            
            IPage<Option> result = page(page, wrapper);
            
            log.info("分页查询选项成功，页码: {}, 大小: {}, 总数: {}", current, size, result.getTotal());
            return result;
        } catch (Exception e) {
            log.error("分页查询选项失败，页码: {}, 大小: {}", current, size, e);
            return new Page<>(current, size);
        }
    }

    @Override
    public List<Option> getOptionsByIds(List<Long> optionIds) {
        if (optionIds == null || optionIds.isEmpty()) {
            return List.of();
        }
        
        try {
            // 使用QueryWrapper替代被移除的selectOptionsByIds方法
            LambdaQueryWrapper<Option> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Option::getId, optionIds)
                   .orderByAsc(Option::getQuestionId)
                   .orderByAsc(Option::getSortOrder);
            
            List<Option> options = list(wrapper);
            log.info("根据ID列表获取选项成功，数量: {}", options.size());
            return options;
        } catch (Exception e) {
            log.error("根据ID列表获取选项失败，IDs: {}", optionIds, e);
            return List.of();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOption(Option option) {
        try {
            // 设置默认值
            if (option.getWeightValue() == null) {
                option.setWeightValue(BigDecimal.valueOf(1.0));
            }
            if (option.getSortOrder() == null) {
                option.setSortOrder(getNextSortOrder(option.getQuestionId()));
            }
            if (option.getIsActive() == null) {
                option.setIsActive(true);
            }
            
            boolean result = save(option);
            
            if (result) {
                log.info("创建选项成功，ID: {}, 内容: {}", option.getId(), option.getContent());
            }
            
            return result;
        } catch (Exception e) {
            log.error("创建选项失败，内容: {}", option.getContent(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOption(Option option) {
        if (option.getId() == null) {
            return false;
        }
        
        try {
            boolean result = updateById(option);
            
            if (result) {
                log.info("更新选项成功，ID: {}, 内容: {}", option.getId(), option.getContent());
            }
            
            return result;
        } catch (Exception e) {
            log.error("更新选项失败，ID: {}", option.getId(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOption(Long id) {
        if (id == null) {
            return false;
        }
        
        try {
            // 软删除：设置为禁用状态
            Option option = new Option();
            option.setId(id);
            option.setIsActive(false);
            
            boolean result = updateById(option);
            
            if (result) {
                log.info("删除选项成功，ID: {}", id);
            }
            
            return result;
        } catch (Exception e) {
            log.error("删除选项失败，ID: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateStatus(List<Long> ids, Boolean isActive) {
        if (ids == null || ids.isEmpty() || isActive == null) {
            return false;
        }
        
        try {
            // 使用UpdateWrapper替代被移除的batchUpdateStatus方法
            LambdaUpdateWrapper<Option> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(Option::getIsActive, isActive)
                   .in(Option::getId, ids);
            
            boolean result = update(wrapper);
            
            if (result) {
                log.info("批量更新选项状态成功，数量: {}, 状态: {}", ids.size(), isActive);
            }
            
            return result;
        } catch (Exception e) {
            log.error("批量更新选项状态失败，IDs: {}, 状态: {}", ids, isActive, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSortOrder(Long id, Integer sortOrder) {
        if (id == null || sortOrder == null) {
            return false;
        }
        
        try {
            int updatedCount = baseMapper.updateSortOrder(id, sortOrder);
            boolean result = updatedCount > 0;
            
            if (result) {
                log.info("更新选项排序成功，ID: {}, 排序值: {}", id, sortOrder);
            }
            
            return result;
        } catch (Exception e) {
            log.error("更新选项排序失败，ID: {}, 排序值: {}", id, sortOrder, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateSortOrder(List<Long> optionIds, List<Integer> sortOrders) {
        if (optionIds == null || sortOrders == null || 
            optionIds.size() != sortOrders.size()) {
            return false;
        }
        
        try {
            for (int i = 0; i < optionIds.size(); i++) {
                updateSortOrder(optionIds.get(i), sortOrders.get(i));
            }
            
            log.info("批量更新选项排序成功，数量: {}", optionIds.size());
            return true;
        } catch (Exception e) {
            log.error("批量更新选项排序失败", e);
            return false;
        }
    }

    @Override
    public List<Option> searchOptionsByTag(String tag) {
        if (!StringUtils.hasText(tag)) {
            return List.of();
        }
        
        try {
            List<Option> options = baseMapper.selectOptionsByTag(tag.trim());
            log.info("根据标签搜索选项成功，标签: {}, 结果数量: {}", tag, options.size());
            return options;
        } catch (Exception e) {
            log.error("根据标签搜索选项失败，标签: {}", tag, e);
            return List.of();
        }
    }

    @Override
    public List<Option> searchOptionsByContent(String content) {
        if (!StringUtils.hasText(content)) {
            return List.of();
        }
        
        try {
            List<Option> options = baseMapper.selectOptionsByContent(content.trim());
            log.info("根据内容搜索选项成功，关键词: {}, 结果数量: {}", content, options.size());
            return options;
        } catch (Exception e) {
            log.error("根据内容搜索选项失败，关键词: {}", content, e);
            return List.of();
        }
    }

    @Override
    public List<Option> getOptionsByWeightRange(BigDecimal minWeight, BigDecimal maxWeight) {
        if (minWeight == null || maxWeight == null) {
            return List.of();
        }
        
        try {
            List<Option> options = baseMapper.selectOptionsByWeightRange(minWeight, maxWeight);
            log.info("根据权重范围查询选项成功，范围: {}-{}, 结果数量: {}", minWeight, maxWeight, options.size());
            return options;
        } catch (Exception e) {
            log.error("根据权重范围查询选项失败，范围: {}-{}", minWeight, maxWeight, e);
            return List.of();
        }
    }

    @Override
    public Integer countActiveOptionsByQuestionId(Long questionId) {
        if (questionId == null) {
            return 0;
        }
        
        try {
            Integer count = baseMapper.countActiveOptionsByQuestionId(questionId);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("统计问题启用选项数量失败，问题ID: {}", questionId, e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getOptionStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总选项数
            statistics.put("totalOptions", count());
            
            // 启用选项数
            long activeCount = lambdaQuery().eq(Option::getIsActive, true).count();
            statistics.put("activeOptions", activeCount);
            
            // 禁用选项数
            statistics.put("inactiveOptions", count() - activeCount);
            
            // 权重范围统计
            List<Map<String, Object>> weightStats = baseMapper.getWeightRangeStatistics();
            statistics.put("weightRangeStats", weightStats);
            
            log.info("获取选项统计信息成功");
            return statistics;
        } catch (Exception e) {
            log.error("获取选项统计信息失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public boolean isSortOrderAvailable(Long questionId, Integer sortOrder, Long excludeId) {
        if (questionId == null || sortOrder == null) {
            return false;
        }
        
        try {
            // 使用QueryWrapper替代被移除的checkSortOrderExists方法
            LambdaQueryWrapper<Option> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Option::getQuestionId, questionId)
                   .eq(Option::getSortOrder, sortOrder);
            if (excludeId != null) {
                wrapper.ne(Option::getId, excludeId);
            }
            
            long count = count(wrapper);
            return count == 0;
        } catch (Exception e) {
            log.error("检查排序值可用性失败，问题ID: {}, 排序值: {}, 排除ID: {}", questionId, sortOrder, excludeId, e);
            return false;
        }
    }

    @Override
    public Integer getNextSortOrder(Long questionId) {
        if (questionId == null) {
            return 1;
        }
        
        try {
            Integer maxOrder = baseMapper.getMaxSortOrderByQuestionId(questionId);
            return maxOrder + 1;
        } catch (Exception e) {
            log.error("获取下一个排序值失败，问题ID: {}", questionId, e);
            return 1;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean copyOptionsByQuestionId(Long sourceQuestionId, Long targetQuestionId) {
        if (sourceQuestionId == null || targetQuestionId == null) {
            return false;
        }
        
        try {
            List<Option> sourceOptions = getAllOptionsByQuestionId(sourceQuestionId);
            
            for (Option sourceOption : sourceOptions) {
                Option newOption = new Option();
                newOption.setQuestionId(targetQuestionId);
                newOption.setContent(sourceOption.getContent());
                newOption.setWeightValue(sourceOption.getWeightValue());
                newOption.setTagKeywords(sourceOption.getTagKeywords());
                newOption.setSortOrder(sourceOption.getSortOrder());
                newOption.setIsActive(true);
                
                save(newOption);
            }
            
            log.info("复制选项成功，源问题ID: {}, 目标问题ID: {}, 数量: {}", 
                    sourceQuestionId, targetQuestionId, sourceOptions.size());
            return true;
        } catch (Exception e) {
            log.error("复制选项失败，源问题ID: {}, 目标问题ID: {}", sourceQuestionId, targetQuestionId, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleOptionStatus(Long id, Boolean isActive) {
        if (id == null || isActive == null) {
            return false;
        }
        
        try {
            Option option = new Option();
            option.setId(id);
            option.setIsActive(isActive);
            
            boolean result = updateById(option);
            
            if (result) {
                log.info("切换选项状态成功，ID: {}, 状态: {}", id, isActive);
            }
            
            return result;
        } catch (Exception e) {
            log.error("切换选项状态失败，ID: {}, 状态: {}", id, isActive, e);
            return false;
        }
    }

    @Override
    public List<Option> getHighWeightOptions() {
        try {
            List<Option> options = baseMapper.selectHighWeightOptions();
            log.info("获取高权重选项成功，数量: {}", options.size());
            return options;
        } catch (Exception e) {
            log.error("获取高权重选项失败", e);
            return List.of();
        }
    }

    @Override
    public List<Map<String, Object>> getWeightRangeStatistics() {
        try {
            List<Map<String, Object>> statistics = baseMapper.getWeightRangeStatistics();
            log.info("获取权重范围统计成功");
            return statistics;
        } catch (Exception e) {
            log.error("获取权重范围统计失败", e);
            return List.of();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreateOptions(List<Option> options) {
        if (options == null || options.isEmpty()) {
            return false;
        }
        
        try {
            for (Option option : options) {
                createOption(option);
            }
            
            log.info("批量创建选项成功，数量: {}", options.size());
            return true;
        } catch (Exception e) {
            log.error("批量创建选项失败", e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOptionsByQuestionId(Long questionId) {
        if (questionId == null) {
            return false;
        }
        
        try {
            int deletedCount = baseMapper.deleteByQuestionId(questionId);
            boolean result = deletedCount >= 0;
            
            if (result) {
                log.info("删除问题所有选项成功，问题ID: {}, 删除数量: {}", questionId, deletedCount);
            }
            
            return result;
        } catch (Exception e) {
            log.error("删除问题所有选项失败，问题ID: {}", questionId, e);
            return false;
        }
    }
}