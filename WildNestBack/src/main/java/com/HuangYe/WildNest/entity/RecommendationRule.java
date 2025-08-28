package com.HuangYe.WildNest.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 推荐规则实体类
 * 
 * @author HuangYe
 * @since 2024
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("recommendation_rule")
@Slf4j
public class RecommendationRule extends BaseEntity {

    /**
     * 规则名称
     */
    @TableField("rule_name")
    private String ruleName;

    /**
     * 规则描述
     */
    @TableField("rule_description")
    private String ruleDescription;

    /**
     * 选项组合，JSON格式存储选项ID数组
     */
    @TableField("option_combination")
    private String optionCombination;

    /**
     * 目标酒品ID列表，逗号分隔
     */
    @TableField("target_drink_ids")
    private String targetDrinkIds;

    /**
     * 匹配分数，用于规则优先级
     */
    @TableField("match_score")
    private BigDecimal matchScore;

    /**
     * 推荐理由，显示给用户
     */
    @TableField("recommendation_reason")
    private String recommendationReason;

    /**
     * 匹配类型：exact-精确匹配，partial-部分匹配，fuzzy-模糊匹配
     */
    @TableField("condition_type")
    private String conditionType;

    /**
     * 最小匹配选项数量
     */
    @TableField("min_match_count")
    private Integer minMatchCount;

    /**
     * 是否启用：1-启用，0-禁用
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 优先级，数值越大优先级越高
     */
    @TableField("priority_level")
    private Integer priorityLevel;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取选项组合列表
     * 
     * @return 选项ID列表
     */
    public List<Long> getOptionCombinationList() {
        if (optionCombination == null || optionCombination.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(optionCombination, new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            log.error("解析选项组合JSON失败: {}", optionCombination, e);
            return new ArrayList<>();
        }
    }

    /**
     * 设置选项组合列表
     * 
     * @param optionIds 选项ID列表
     */
    public void setOptionCombinationList(List<Long> optionIds) {
        if (optionIds == null || optionIds.isEmpty()) {
            this.optionCombination = "[]";
        } else {
            try {
                this.optionCombination = objectMapper.writeValueAsString(optionIds);
            } catch (JsonProcessingException e) {
                log.error("序列化选项组合失败: {}", optionIds, e);
                this.optionCombination = "[]";
            }
        }
    }

    /**
     * 获取目标酒品ID列表
     * 
     * @return 酒品ID列表
     */
    public List<Long> getTargetDrinkIdList() {
        if (targetDrinkIds == null || targetDrinkIds.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return Arrays.stream(targetDrinkIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .toList();
        } catch (NumberFormatException e) {
            log.error("解析目标酒品ID失败: {}", targetDrinkIds, e);
            return new ArrayList<>();
        }
    }

    /**
     * 设置目标酒品ID列表
     * 
     * @param drinkIds 酒品ID列表
     */
    public void setTargetDrinkIdList(List<Long> drinkIds) {
        if (drinkIds == null || drinkIds.isEmpty()) {
            this.targetDrinkIds = "";
        } else {
            this.targetDrinkIds = drinkIds.stream()
                    .map(String::valueOf)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
        }
    }

    /**
     * 获取匹配分数的double类型
     * 
     * @return 匹配分数
     */
    public double getMatchScoreAsDouble() {
        return matchScore != null ? matchScore.doubleValue() : 0.0;
    }

    /**
     * 检查是否包含指定选项
     * 
     * @param optionId 选项ID
     * @return 是否包含
     */
    public boolean containsOption(Long optionId) {
        if (optionId == null) {
            return false;
        }
        List<Long> options = getOptionCombinationList();
        return options.contains(optionId);
    }

    /**
     * 匹配类型枚举
     */
    public enum ConditionType {
        EXACT("exact", "精确匹配"),
        PARTIAL("partial", "部分匹配"),
        FUZZY("fuzzy", "模糊匹配");

        private final String code;
        private final String description;

        ConditionType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static ConditionType fromCode(String code) {
            for (ConditionType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return EXACT; // 默认精确匹配
        }
    }
}