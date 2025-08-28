package com.HuangYe.WildNest.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推荐日志实体类
 * 
 * @author HuangYe
 * @since 2024
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("recommendation_log")
@Slf4j
public class RecommendationLog {

    /**
     * 日志ID，主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID，用于追踪用户推荐会话
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * 用户答案，JSON格式存储问题ID和选项ID的映射
     */
    @TableField("user_answers")
    private String userAnswers;

    /**
     * 推荐结果，JSON格式存储推荐的酒品信息
     */
    @TableField("recommended_drinks")
    private String recommendedDrinks;

    /**
     * 匹配的规则，JSON格式存储匹配到的规则信息
     */
    @TableField("matched_rules")
    private String matchedRules;

    /**
     * 算法版本号
     */
    @TableField("algorithm_version")
    private String algorithmVersion;

    /**
     * 总推荐分数
     */
    @TableField("total_score")
    private BigDecimal totalScore;

    /**
     * 算法执行时间（毫秒）
     */
    @TableField("execution_time_ms")
    private Integer executionTimeMs;

    /**
     * 用户IP地址
     */
    @TableField("user_ip")
    private String userIp;

    /**
     * 用户代理信息
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 设备信息
     */
    @TableField("device_info")
    private String deviceInfo;

    /**
     * 用户反馈：1-满意，0-不满意，NULL-未反馈
     */
    @TableField("user_feedback")
    private Integer userFeedback;

    /**
     * 反馈原因
     */
    @TableField("feedback_reason")
    private String feedbackReason;

    /**
     * 是否测试数据：1-是，0-否
     */
    @TableField("is_test_data")
    private Boolean isTestData;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取用户答案Map
     * 
     * @return 问题ID -> 选项ID列表的映射
     */
    public Map<Long, List<Long>> getUserAnswersMap() {
        if (userAnswers == null || userAnswers.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(userAnswers, new TypeReference<Map<Long, List<Long>>>() {});
        } catch (JsonProcessingException e) {
            log.error("解析用户答案JSON失败: {}", userAnswers, e);
            return new HashMap<>();
        }
    }

    /**
     * 设置用户答案Map
     * 
     * @param answersMap 问题ID -> 选项ID列表的映射
     */
    public void setUserAnswersMap(Map<Long, List<Long>> answersMap) {
        if (answersMap == null || answersMap.isEmpty()) {
            this.userAnswers = "{}";
        } else {
            try {
                this.userAnswers = objectMapper.writeValueAsString(answersMap);
            } catch (JsonProcessingException e) {
                log.error("序列化用户答案失败: {}", answersMap, e);
                this.userAnswers = "{}";
            }
        }
    }

    /**
     * 获取推荐结果列表
     * 
     * @return 推荐结果列表
     */
    public List<Map<String, Object>> getRecommendedDrinksList() {
        if (recommendedDrinks == null || recommendedDrinks.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(recommendedDrinks, new TypeReference<List<Map<String, Object>>>() {});
        } catch (JsonProcessingException e) {
            log.error("解析推荐结果JSON失败: {}", recommendedDrinks, e);
            return new ArrayList<>();
        }
    }

    /**
     * 设置推荐结果列表
     * 
     * @param drinksList 推荐结果列表
     */
    public void setRecommendedDrinksList(List<Map<String, Object>> drinksList) {
        if (drinksList == null || drinksList.isEmpty()) {
            this.recommendedDrinks = "[]";
        } else {
            try {
                this.recommendedDrinks = objectMapper.writeValueAsString(drinksList);
            } catch (JsonProcessingException e) {
                log.error("序列化推荐结果失败: {}", drinksList, e);
                this.recommendedDrinks = "[]";
            }
        }
    }

    /**
     * 获取匹配规则列表
     * 
     * @return 匹配规则列表
     */
    public List<Map<String, Object>> getMatchedRulesList() {
        if (matchedRules == null || matchedRules.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(matchedRules, new TypeReference<List<Map<String, Object>>>() {});
        } catch (JsonProcessingException e) {
            log.error("解析匹配规则JSON失败: {}", matchedRules, e);
            return new ArrayList<>();
        }
    }

    /**
     * 设置匹配规则列表
     * 
     * @param rulesList 匹配规则列表
     */
    public void setMatchedRulesList(List<Map<String, Object>> rulesList) {
        if (rulesList == null || rulesList.isEmpty()) {
            this.matchedRules = "[]";
        } else {
            try {
                this.matchedRules = objectMapper.writeValueAsString(rulesList);
            } catch (JsonProcessingException e) {
                log.error("序列化匹配规则失败: {}", rulesList, e);
                this.matchedRules = "[]";
            }
        }
    }

    /**
     * 获取总分数的double类型
     * 
     * @return 总分数
     */
    public double getTotalScoreAsDouble() {
        return totalScore != null ? totalScore.doubleValue() : 0.0;
    }

    /**
     * 用户反馈枚举
     */
    public enum UserFeedback {
        SATISFIED(1, "满意"),
        UNSATISFIED(0, "不满意");

        private final Integer code;
        private final String description;

        UserFeedback(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static UserFeedback fromCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (UserFeedback feedback : values()) {
                if (feedback.code.equals(code)) {
                    return feedback;
                }
            }
            return null;
        }
    }
}