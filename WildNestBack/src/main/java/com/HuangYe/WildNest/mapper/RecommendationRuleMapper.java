package com.HuangYe.WildNest.mapper;

import com.HuangYe.WildNest.entity.RecommendationRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推荐规则Mapper接口
 * 
 * @author HuangYe
 * @since 2024
 */
@Mapper
public interface RecommendationRuleMapper extends BaseMapper<RecommendationRule> {

    /**
     * 查询所有启用的规则，按优先级和匹配分数排序
     * 
     * @return 规则列表
     */
    @Select("SELECT * FROM recommendation_rule WHERE is_active = 1 " +
            "ORDER BY priority_level DESC, match_score DESC, created_at ASC")
    List<RecommendationRule> selectActiveRules();

    /**
     * 分页查询规则（管理员用）
     * 注意：此方法已移除复杂的动态SQL注解，改为在Service层使用QueryWrapper实现
     * 
     * @param page 分页参数
     * @return 规则分页数据
     */
    // 移除动态SQL，改为在Service层使用QueryWrapper
    // IPage<RecommendationRule> selectRulesPage(Page<RecommendationRule> page,
    //                                          @Param("conditionType") String conditionType,
    //                                          @Param("isActive") Boolean isActive);

    /**
     * 根据匹配类型查询启用的规则
     * 
     * @param conditionType 匹配类型
     * @return 规则列表
     */
    @Select("SELECT * FROM recommendation_rule WHERE is_active = 1 AND condition_type = #{conditionType} " +
            "ORDER BY priority_level DESC, match_score DESC")
    List<RecommendationRule> selectActiveRulesByConditionType(@Param("conditionType") String conditionType);

    /**
     * 根据优先级范围查询规则
     * 
     * @param minPriority 最小优先级
     * @param maxPriority 最大优先级
     * @return 规则列表
     */
    @Select("SELECT * FROM recommendation_rule WHERE is_active = 1 " +
            "AND priority_level >= #{minPriority} AND priority_level <= #{maxPriority} " +
            "ORDER BY priority_level DESC, match_score DESC")
    List<RecommendationRule> selectRulesByPriorityRange(@Param("minPriority") Integer minPriority,
                                                        @Param("maxPriority") Integer maxPriority);

    /**
     * 根据匹配分数范围查询规则
     * 
     * @param minScore 最小匹配分数
     * @param maxScore 最大匹配分数
     * @return 规则列表
     */
    @Select("SELECT * FROM recommendation_rule WHERE is_active = 1 " +
            "AND match_score >= #{minScore} AND match_score <= #{maxScore} " +
            "ORDER BY match_score DESC, priority_level DESC")
    List<RecommendationRule> selectRulesByScoreRange(@Param("minScore") BigDecimal minScore,
                                                     @Param("maxScore") BigDecimal maxScore);

    /**
     * 根据规则名称模糊查询
     * 
     * @param ruleName 规则名称关键词
     * @return 规则列表
     */
    @Select("SELECT * FROM recommendation_rule WHERE rule_name LIKE CONCAT('%', #{ruleName}, '%') " +
            "ORDER BY priority_level DESC, match_score DESC")
    List<RecommendationRule> selectRulesByName(@Param("ruleName") String ruleName);

    /**
     * 查询包含指定酒品ID的规则
     * 
     * @param drinkId 酒品ID
     * @return 规则列表
     */
    @Select("SELECT * FROM recommendation_rule WHERE is_active = 1 " +
            "AND FIND_IN_SET(#{drinkId}, target_drink_ids) > 0 " +
            "ORDER BY priority_level DESC, match_score DESC")
    List<RecommendationRule> selectRulesByDrinkId(@Param("drinkId") Long drinkId);

    /**
     * 获取最高优先级值
     * 
     * @return 最高优先级值
     */
    @Select("SELECT COALESCE(MAX(priority_level), 0) FROM recommendation_rule")
    Integer getMaxPriorityLevel();

    /**
     * 批量更新规则状态
     * 注意：此方法已移除复杂的动态SQL注解，改为在Service层使用QueryWrapper实现
     * 
     * @param ids 规则ID列表
     * @param isActive 是否启用
     * @return 更新行数
     */
    // 移除动态SQL，改为在Service层使用QueryWrapper
    // int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("isActive") Boolean isActive);

    /**
     * 更新规则优先级
     * 
     * @param id 规则ID
     * @param priorityLevel 新的优先级
     * @return 更新行数
     */
    @Update("UPDATE recommendation_rule SET priority_level = #{priorityLevel} WHERE id = #{id}")
    int updatePriorityLevel(@Param("id") Long id, @Param("priorityLevel") Integer priorityLevel);

    /**
     * 更新规则匹配分数
     * 
     * @param id 规则ID
     * @param matchScore 新的匹配分数
     * @return 更新行数
     */
    @Update("UPDATE recommendation_rule SET match_score = #{matchScore} WHERE id = #{id}")
    int updateMatchScore(@Param("id") Long id, @Param("matchScore") BigDecimal matchScore);

    /**
     * 统计启用的规则数量
     * 
     * @return 规则数量
     */
    @Select("SELECT COUNT(*) FROM recommendation_rule WHERE is_active = 1")
    Integer countActiveRules();

    /**
     * 统计各匹配类型的规则数量
     * 
     * @return 统计结果
     */
    @Select("SELECT condition_type, COUNT(*) as count " +
            "FROM recommendation_rule WHERE is_active = 1 " +
            "GROUP BY condition_type")
    List<java.util.Map<String, Object>> getConditionTypeStatistics();

    /**
     * 查询高优先级规则（优先级大于5）
     * 
     * @return 规则列表
     */
    @Select("SELECT * FROM recommendation_rule WHERE is_active = 1 AND priority_level > 5 " +
            "ORDER BY priority_level DESC, match_score DESC")
    List<RecommendationRule> selectHighPriorityRules();

    /**
     * 查询精确匹配规则
     * 
     * @return 规则列表
     */
    @Select("SELECT * FROM recommendation_rule WHERE is_active = 1 AND condition_type = 'exact' " +
            "ORDER BY priority_level DESC, match_score DESC")
    List<RecommendationRule> selectExactMatchRules();

    /**
     * 查询部分匹配规则
     * 
     * @return 规则列表
     */
    @Select("SELECT * FROM recommendation_rule WHERE is_active = 1 AND condition_type = 'partial' " +
            "ORDER BY priority_level DESC, match_score DESC")
    List<RecommendationRule> selectPartialMatchRules();

    /**
     * 查询模糊匹配规则
     * 
     * @return 规则列表
     */
    @Select("SELECT * FROM recommendation_rule WHERE is_active = 1 AND condition_type = 'fuzzy' " +
            "ORDER BY priority_level DESC, match_score DESC")
    List<RecommendationRule> selectFuzzyMatchRules();

    /**
     * 检查规则名称是否已存在
     * 注意：此方法已移除复杂的动态SQL注解，改为在Service层使用QueryWrapper实现
     * 
     * @param ruleName 规则名称
     * @param excludeId 排除的规则ID（用于更新时检查）
     * @return 是否存在
     */
    // 移除动态SQL，改为在Service层使用QueryWrapper
    // Integer checkRuleNameExists(@Param("ruleName") String ruleName,
    //                            @Param("excludeId") Long excludeId);

    /**
     * 根据最小匹配数量查询规则
     * 
     * @param minMatchCount 最小匹配数量
     * @return 规则列表
     */
    @Select("SELECT * FROM recommendation_rule WHERE is_active = 1 " +
            "AND min_match_count <= #{minMatchCount} " +
            "ORDER BY priority_level DESC, match_score DESC")
    List<RecommendationRule> selectRulesByMinMatchCount(@Param("minMatchCount") Integer minMatchCount);
}