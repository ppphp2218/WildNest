package com.HuangYe.WildNest.service.impl;

import com.HuangYe.WildNest.entity.RecommendationRule;
import com.HuangYe.WildNest.mapper.RecommendationRuleMapper;
import com.HuangYe.WildNest.service.RecommendationRuleService;
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
 * 推荐规则服务实现类
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRuleServiceImpl extends ServiceImpl<RecommendationRuleMapper, RecommendationRule> implements RecommendationRuleService {

    @Override
    public List<RecommendationRule> getAllActiveRules() {
        try {
            List<RecommendationRule> rules = baseMapper.selectActiveRules();
            log.info("获取所有启用规则成功，数量: {}", rules.size());
            return rules;
        } catch (Exception e) {
            log.error("获取所有启用规则失败", e);
            return List.of();
        }
    }

    @Override
    public List<RecommendationRule> getActiveRulesByConditionType(String conditionType) {
        if (!StringUtils.hasText(conditionType)) {
            return getAllActiveRules();
        }
        
        try {
            List<RecommendationRule> rules = baseMapper.selectActiveRulesByConditionType(conditionType);
            log.info("根据匹配类型获取规则成功，类型: {}, 数量: {}", conditionType, rules.size());
            return rules;
        } catch (Exception e) {
            log.error("根据匹配类型获取规则失败，类型: {}", conditionType, e);
            return List.of();
        }
    }

    @Override
    public IPage<RecommendationRule> getRulesPage(Long current, Long size, String conditionType, Boolean isActive) {
        try {
            Page<RecommendationRule> page = new Page<>(current, size);
            
            // 使用LambdaQueryWrapper构建动态查询条件
            LambdaQueryWrapper<RecommendationRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StringUtils.hasText(conditionType), RecommendationRule::getConditionType, conditionType)
                   .eq(isActive != null, RecommendationRule::getIsActive, isActive)
                   .orderByDesc(RecommendationRule::getPriorityLevel)
                   .orderByDesc(RecommendationRule::getMatchScore)
                   .orderByDesc(RecommendationRule::getCreatedAt);
            
            IPage<RecommendationRule> result = page(page, wrapper);
            
            log.info("分页查询推荐规则成功，页码: {}, 大小: {}, 总数: {}", current, size, result.getTotal());
            return result;
        } catch (Exception e) {
            log.error("分页查询推荐规则失败，页码: {}, 大小: {}", current, size, e);
            return new Page<>(current, size);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRule(RecommendationRule rule) {
        try {
            // 验证规则
            if (!validateRule(rule)) {
                return false;
            }
            
            // 设置默认值
            if (rule.getConditionType() == null) {
                rule.setConditionType(RecommendationRule.ConditionType.EXACT.getCode());
            }
            if (rule.getMinMatchCount() == null) {
                rule.setMinMatchCount(1);
            }
            if (rule.getPriorityLevel() == null) {
                rule.setPriorityLevel(getNextPriorityLevel());
            }
            if (rule.getIsActive() == null) {
                rule.setIsActive(true);
            }
            if (rule.getMatchScore() == null) {
                rule.setMatchScore(BigDecimal.valueOf(50.0));
            }
            
            boolean result = save(rule);
            
            if (result) {
                log.info("创建推荐规则成功，ID: {}, 名称: {}", rule.getId(), rule.getRuleName());
            }
            
            return result;
        } catch (Exception e) {
            log.error("创建推荐规则失败，名称: {}", rule.getRuleName(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRule(RecommendationRule rule) {
        if (rule.getId() == null) {
            return false;
        }
        
        try {
            // 验证规则
            if (!validateRule(rule)) {
                return false;
            }
            
            boolean result = updateById(rule);
            
            if (result) {
                log.info("更新推荐规则成功，ID: {}, 名称: {}", rule.getId(), rule.getRuleName());
            }
            
            return result;
        } catch (Exception e) {
            log.error("更新推荐规则失败，ID: {}", rule.getId(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRule(Long id) {
        if (id == null) {
            return false;
        }
        
        try {
            // 软删除：设置为禁用状态
            RecommendationRule rule = new RecommendationRule();
            rule.setId(id);
            rule.setIsActive(false);
            
            boolean result = updateById(rule);
            
            if (result) {
                log.info("删除推荐规则成功，ID: {}", id);
            }
            
            return result;
        } catch (Exception e) {
            log.error("删除推荐规则失败，ID: {}", id, e);
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
            LambdaUpdateWrapper<RecommendationRule> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(RecommendationRule::getIsActive, isActive)
                   .in(RecommendationRule::getId, ids);
            
            boolean result = update(wrapper);
            
            if (result) {
                log.info("批量更新规则状态成功，数量: {}, 状态: {}", ids.size(), isActive);
            }
            
            return result;
        } catch (Exception e) {
            log.error("批量更新规则状态失败，IDs: {}, 状态: {}", ids, isActive, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePriorityLevel(Long id, Integer priorityLevel) {
        if (id == null || priorityLevel == null) {
            return false;
        }
        
        try {
            int updatedCount = baseMapper.updatePriorityLevel(id, priorityLevel);
            boolean result = updatedCount > 0;
            
            if (result) {
                log.info("更新规则优先级成功，ID: {}, 优先级: {}", id, priorityLevel);
            }
            
            return result;
        } catch (Exception e) {
            log.error("更新规则优先级失败，ID: {}, 优先级: {}", id, priorityLevel, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMatchScore(Long id, BigDecimal matchScore) {
        if (id == null || matchScore == null) {
            return false;
        }
        
        try {
            int updatedCount = baseMapper.updateMatchScore(id, matchScore);
            boolean result = updatedCount > 0;
            
            if (result) {
                log.info("更新规则匹配分数成功，ID: {}, 分数: {}", id, matchScore);
            }
            
            return result;
        } catch (Exception e) {
            log.error("更新规则匹配分数失败，ID: {}, 分数: {}", id, matchScore, e);
            return false;
        }
    }

    @Override
    public List<RecommendationRule> searchRulesByName(String ruleName) {
        if (!StringUtils.hasText(ruleName)) {
            return List.of();
        }
        
        try {
            List<RecommendationRule> rules = baseMapper.selectRulesByName(ruleName.trim());
            log.info("根据名称搜索规则成功，关键词: {}, 结果数量: {}", ruleName, rules.size());
            return rules;
        } catch (Exception e) {
            log.error("根据名称搜索规则失败，关键词: {}", ruleName, e);
            return List.of();
        }
    }

    @Override
    public List<RecommendationRule> getRulesByDrinkId(Long drinkId) {
        if (drinkId == null) {
            return List.of();
        }
        
        try {
            List<RecommendationRule> rules = baseMapper.selectRulesByDrinkId(drinkId);
            log.info("根据酒品ID查询规则成功，酒品ID: {}, 结果数量: {}", drinkId, rules.size());
            return rules;
        } catch (Exception e) {
            log.error("根据酒品ID查询规则失败，酒品ID: {}", drinkId, e);
            return List.of();
        }
    }

    @Override
    public Map<String, Object> getRuleStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总规则数
            statistics.put("totalRules", count());
            
            // 启用规则数
            Integer activeCount = baseMapper.countActiveRules();
            statistics.put("activeRules", activeCount);
            
            // 禁用规则数
            statistics.put("inactiveRules", count() - activeCount);
            
            // 各匹配类型统计
            List<Map<String, Object>> conditionTypeStats = baseMapper.getConditionTypeStatistics();
            statistics.put("conditionTypeStats", conditionTypeStats);
            
            log.info("获取规则统计信息成功");
            return statistics;
        } catch (Exception e) {
            log.error("获取规则统计信息失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public boolean isRuleNameAvailable(String ruleName, Long excludeId) {
        if (!StringUtils.hasText(ruleName)) {
            return false;
        }
        
        try {
            // 使用QueryWrapper替代被移除的checkRuleNameExists方法
            LambdaQueryWrapper<RecommendationRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RecommendationRule::getRuleName, ruleName.trim());
            if (excludeId != null) {
                wrapper.ne(RecommendationRule::getId, excludeId);
            }
            
            long count = count(wrapper);
            return count == 0;
        } catch (Exception e) {
            log.error("检查规则名称可用性失败，名称: {}, 排除ID: {}", ruleName, excludeId, e);
            return false;
        }
    }

    @Override
    public Integer getNextPriorityLevel() {
        try {
            Integer maxPriority = baseMapper.getMaxPriorityLevel();
            return maxPriority + 1;
        } catch (Exception e) {
            log.error("获取下一个优先级失败", e);
            return 1;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleRuleStatus(Long id, Boolean isActive) {
        if (id == null || isActive == null) {
            return false;
        }
        
        try {
            RecommendationRule rule = new RecommendationRule();
            rule.setId(id);
            rule.setIsActive(isActive);
            
            boolean result = updateById(rule);
            
            if (result) {
                log.info("切换规则状态成功，ID: {}, 状态: {}", id, isActive);
            }
            
            return result;
        } catch (Exception e) {
            log.error("切换规则状态失败，ID: {}, 状态: {}", id, isActive, e);
            return false;
        }
    }

    @Override
    public List<RecommendationRule> getExactMatchRules() {
        try {
            List<RecommendationRule> rules = baseMapper.selectExactMatchRules();
            log.info("获取精确匹配规则成功，数量: {}", rules.size());
            return rules;
        } catch (Exception e) {
            log.error("获取精确匹配规则失败", e);
            return List.of();
        }
    }

    @Override
    public List<RecommendationRule> getPartialMatchRules() {
        try {
            List<RecommendationRule> rules = baseMapper.selectPartialMatchRules();
            log.info("获取部分匹配规则成功，数量: {}", rules.size());
            return rules;
        } catch (Exception e) {
            log.error("获取部分匹配规则失败", e);
            return List.of();
        }
    }

    @Override
    public List<RecommendationRule> getFuzzyMatchRules() {
        try {
            List<RecommendationRule> rules = baseMapper.selectFuzzyMatchRules();
            log.info("获取模糊匹配规则成功，数量: {}", rules.size());
            return rules;
        } catch (Exception e) {
            log.error("获取模糊匹配规则失败", e);
            return List.of();
        }
    }

    @Override
    public List<RecommendationRule> getHighPriorityRules() {
        try {
            List<RecommendationRule> rules = baseMapper.selectHighPriorityRules();
            log.info("获取高优先级规则成功，数量: {}", rules.size());
            return rules;
        } catch (Exception e) {
            log.error("获取高优先级规则失败", e);
            return List.of();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean copyRule(Long id, String newRuleName) {
        if (id == null || !StringUtils.hasText(newRuleName)) {
            return false;
        }
        
        try {
            RecommendationRule originalRule = getById(id);
            if (originalRule == null) {
                return false;
            }
            
            // 创建新规则
            RecommendationRule newRule = new RecommendationRule();
            newRule.setRuleName(newRuleName);
            newRule.setRuleDescription(originalRule.getRuleDescription());
            newRule.setOptionCombination(originalRule.getOptionCombination());
            newRule.setTargetDrinkIds(originalRule.getTargetDrinkIds());
            newRule.setMatchScore(originalRule.getMatchScore());
            newRule.setRecommendationReason(originalRule.getRecommendationReason());
            newRule.setConditionType(originalRule.getConditionType());
            newRule.setMinMatchCount(originalRule.getMinMatchCount());
            newRule.setPriorityLevel(getNextPriorityLevel());
            newRule.setIsActive(true);
            
            boolean result = save(newRule);
            
            if (result) {
                log.info("复制规则成功，原ID: {}, 新ID: {}, 新名称: {}", id, newRule.getId(), newRuleName);
            }
            
            return result;
        } catch (Exception e) {
            log.error("复制规则失败，ID: {}, 新名称: {}", id, newRuleName, e);
            return false;
        }
    }

    @Override
    public boolean validateRule(RecommendationRule rule) {
        if (rule == null) {
            log.warn("规则验证失败：规则为空");
            return false;
        }
        
        // 验证规则名称
        if (!StringUtils.hasText(rule.getRuleName())) {
            log.warn("规则验证失败：规则名称为空");
            return false;
        }
        
        // 验证规则名称唯一性
        if (!isRuleNameAvailable(rule.getRuleName(), rule.getId())) {
            log.warn("规则验证失败：规则名称已存在，名称: {}", rule.getRuleName());
            return false;
        }
        
        // 验证选项组合
        if (!StringUtils.hasText(rule.getOptionCombination())) {
            log.warn("规则验证失败：选项组合为空");
            return false;
        }
        
        // 验证目标酒品ID
        if (!StringUtils.hasText(rule.getTargetDrinkIds())) {
            log.warn("规则验证失败：目标酒品ID为空");
            return false;
        }
        
        // 验证匹配分数
        if (rule.getMatchScore() != null && 
            (rule.getMatchScore().compareTo(BigDecimal.ZERO) < 0 || 
             rule.getMatchScore().compareTo(BigDecimal.valueOf(100)) > 0)) {
            log.warn("规则验证失败：匹配分数超出范围(0-100)，分数: {}", rule.getMatchScore());
            return false;
        }
        
        // 验证最小匹配数量
        if (rule.getMinMatchCount() != null && rule.getMinMatchCount() < 1) {
            log.warn("规则验证失败：最小匹配数量不能小于1，数量: {}", rule.getMinMatchCount());
            return false;
        }
        
        log.debug("规则验证通过，名称: {}", rule.getRuleName());
        return true;
    }
}