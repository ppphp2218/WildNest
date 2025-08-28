package com.HuangYe.WildNest.service;

import com.HuangYe.WildNest.entity.RecommendationRule;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推荐规则服务接口
 * 
 * @author HuangYe
 * @since 2024
 */
public interface RecommendationRuleService extends IService<RecommendationRule> {

    /**
     * 获取所有启用的规则
     * 
     * @return 规则列表
     */
    List<RecommendationRule> getAllActiveRules();

    /**
     * 根据匹配类型获取启用的规则
     * 
     * @param conditionType 匹配类型
     * @return 规则列表
     */
    List<RecommendationRule> getActiveRulesByConditionType(String conditionType);

    /**
     * 分页查询规则（管理员用）
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param conditionType 匹配类型（可选）
     * @param isActive 是否启用（可选）
     * @return 规则分页数据
     */
    IPage<RecommendationRule> getRulesPage(Long current, Long size, String conditionType, Boolean isActive);

    /**
     * 创建推荐规则
     * 
     * @param rule 规则信息
     * @return 创建结果
     */
    boolean createRule(RecommendationRule rule);

    /**
     * 更新推荐规则
     * 
     * @param rule 规则信息
     * @return 更新结果
     */
    boolean updateRule(RecommendationRule rule);

    /**
     * 删除推荐规则（软删除）
     * 
     * @param id 规则ID
     * @return 删除结果
     */
    boolean deleteRule(Long id);

    /**
     * 批量更新规则状态
     * 
     * @param ids 规则ID列表
     * @param isActive 是否启用
     * @return 更新结果
     */
    boolean batchUpdateStatus(List<Long> ids, Boolean isActive);

    /**
     * 更新规则优先级
     * 
     * @param id 规则ID
     * @param priorityLevel 新的优先级
     * @return 更新结果
     */
    boolean updatePriorityLevel(Long id, Integer priorityLevel);

    /**
     * 更新规则匹配分数
     * 
     * @param id 规则ID
     * @param matchScore 新的匹配分数
     * @return 更新结果
     */
    boolean updateMatchScore(Long id, BigDecimal matchScore);

    /**
     * 根据规则名称搜索
     * 
     * @param ruleName 规则名称关键词
     * @return 规则列表
     */
    List<RecommendationRule> searchRulesByName(String ruleName);

    /**
     * 根据酒品ID查询相关规则
     * 
     * @param drinkId 酒品ID
     * @return 规则列表
     */
    List<RecommendationRule> getRulesByDrinkId(Long drinkId);

    /**
     * 获取规则统计信息
     * 
     * @return 统计信息
     */
    java.util.Map<String, Object> getRuleStatistics();

    /**
     * 检查规则名称是否可用
     * 
     * @param ruleName 规则名称
     * @param excludeId 排除的规则ID（用于更新时检查）
     * @return 是否可用
     */
    boolean isRuleNameAvailable(String ruleName, Long excludeId);

    /**
     * 获取下一个可用的优先级
     * 
     * @return 优先级值
     */
    Integer getNextPriorityLevel();

    /**
     * 启用/禁用规则
     * 
     * @param id 规则ID
     * @param isActive 是否启用
     * @return 操作结果
     */
    boolean toggleRuleStatus(Long id, Boolean isActive);

    /**
     * 获取精确匹配规则
     * 
     * @return 规则列表
     */
    List<RecommendationRule> getExactMatchRules();

    /**
     * 获取部分匹配规则
     * 
     * @return 规则列表
     */
    List<RecommendationRule> getPartialMatchRules();

    /**
     * 获取模糊匹配规则
     * 
     * @return 规则列表
     */
    List<RecommendationRule> getFuzzyMatchRules();

    /**
     * 获取高优先级规则
     * 
     * @return 规则列表
     */
    List<RecommendationRule> getHighPriorityRules();

    /**
     * 复制规则
     * 
     * @param id 原规则ID
     * @param newRuleName 新规则名称
     * @return 复制结果
     */
    boolean copyRule(Long id, String newRuleName);

    /**
     * 验证规则配置
     * 
     * @param rule 规则信息
     * @return 验证结果
     */
    boolean validateRule(RecommendationRule rule);
}