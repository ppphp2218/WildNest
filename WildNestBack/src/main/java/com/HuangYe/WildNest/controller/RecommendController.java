package com.HuangYe.WildNest.controller;

import com.HuangYe.WildNest.common.Result;
import com.HuangYe.WildNest.entity.*;
import com.HuangYe.WildNest.service.*;
import com.HuangYe.WildNest.service.recommendation.RecommendationEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务控制器
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
@Validated
@Tag(name = "推荐服务", description = "酒品推荐相关接口")
public class RecommendController extends BaseController {

    private final QuestionService questionService;
    private final OptionService optionService;
    private final RecommendationRuleService recommendationRuleService;
    private final RecommendationLogService recommendationLogService;
    private final DrinkService drinkService;
    private final RecommendationEngine recommendationEngine;

    /**
     * 获取推荐问题列表
     */
    @GetMapping("/questions")
    @Operation(summary = "获取推荐问题", description = "获取所有启用的推荐问题和选项")
    public Result<List<Question>> getQuestions() {
        try {
            List<Question> questions = questionService.getAllActiveQuestions();
            
            if (questions.isEmpty()) {
                return Result.error("暂无可用的推荐问题");
            }
            
            log.info("获取推荐问题成功，数量: {}", questions.size());
            return Result.success(questions);
        } catch (Exception e) {
            log.error("获取推荐问题失败", e);
            return Result.error("获取推荐问题失败");
        }
    }

    /**
     * 提交推荐请求
     */
    @PostMapping("/result")
    @Operation(summary = "获取推荐结果", description = "根据用户答案获取推荐的酒品")
    public Result<Map<String, Object>> getRecommendation(
            @Parameter(description = "推荐请求", required = true)
            @RequestBody RecommendationRequest request,
            HttpServletRequest httpRequest) {
        try {
            // 验证请求参数
            if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
                return Result.error("请完成所有问题的回答");
            }
            
            log.info("收到推荐请求，会话ID: {}, 答案数量: {}", 
                    request.getSessionId(), request.getAnswers().size());
            
            // 转换用户答案格式
            Map<Long, List<Long>> userAnswers = convertAnswers(request.getAnswers());
            
            // 获取所有必要数据
            List<Option> allOptions = optionService.list();
            List<RecommendationRule> allRules = recommendationRuleService.getAllActiveRules();
            List<Drink> allDrinks = drinkService.list();
            
            // 执行推荐算法
            RecommendationEngine.RecommendationResult result = recommendationEngine.recommend(
                    userAnswers, allOptions, allRules, allDrinks);
            
            // 记录推荐日志
            saveRecommendationLog(request, result, httpRequest);
            
            // 构建响应数据
            Map<String, Object> response = buildRecommendationResponse(result);
            
            log.info("推荐请求处理成功，会话ID: {}, 推荐酒品数量: {}, 总分数: {}", 
                    request.getSessionId(), result.getRecommendedDrinks().size(), result.getTotalScore());
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("推荐请求处理失败，会话ID: {}", request.getSessionId(), e);
            return Result.error("推荐服务暂时不可用，请稍后重试");
        }
    }

    /**
     * 获取推荐结果详情
     */
    @GetMapping("/result/{id}")
    @Operation(summary = "获取推荐结果详情", description = "根据推荐日志ID获取详细的推荐结果")
    public Result<Map<String, Object>> getRecommendationDetail(
            @Parameter(description = "推荐日志ID", required = true)
            @PathVariable Long id) {
        try {
            if (id == null) {
                return Result.error("推荐记录ID不能为空");
            }
            
            // 获取推荐日志
            RecommendationLog recommendationLog = recommendationLogService.getById(id);
            if (recommendationLog == null) {
                return Result.error("推荐记录不存在");
            }
            
            // 构建详细响应数据
            Map<String, Object> response = new HashMap<>();
            
            // 基本信息
            response.put("id", recommendationLog.getId());
            response.put("sessionId", recommendationLog.getSessionId());
            response.put("algorithmVersion", recommendationLog.getAlgorithmVersion());
            response.put("totalScore", recommendationLog.getTotalScoreAsDouble());
            response.put("executionTime", recommendationLog.getExecutionTimeMs());
            response.put("createdAt", recommendationLog.getCreatedAt());
            response.put("userFeedback", recommendationLog.getUserFeedback());
            response.put("feedbackReason", recommendationLog.getFeedbackReason());
            
            // 用户答案
            Map<Long, List<Long>> userAnswers = recommendationLog.getUserAnswersMap();
            response.put("userAnswers", userAnswers);
            
            // 推荐结果
            List<Map<String, Object>> recommendedDrinks = recommendationLog.getRecommendedDrinksList();
            response.put("recommendedDrinks", recommendedDrinks);
            response.put("recommendationCount", recommendedDrinks.size());
            
            // 匹配规则
            List<Map<String, Object>> matchedRules = recommendationLog.getMatchedRulesList();
            response.put("matchedRules", matchedRules);
            response.put("ruleCount", matchedRules.size());
            
            // 获取问题和选项详情（用于显示用户的具体选择）
            Map<String, Object> answerDetails = buildAnswerDetails(userAnswers);
            response.put("answerDetails", answerDetails);
            
            log.info("获取推荐结果详情成功，ID: {}", id);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取推荐结果详情失败，ID: {}", id, e);
            return Result.error("获取推荐结果详情失败");
        }
    }

    /**
     * 获取推荐历史
     */
    @GetMapping("/history")
    @Operation(summary = "获取推荐历史", description = "根据会话ID获取推荐历史")
    public Result<List<RecommendationLog>> getRecommendationHistory(
            @Parameter(description = "会话ID", required = true)
            @RequestParam String sessionId) {
        try {
            if (sessionId == null || sessionId.trim().isEmpty()) {
                return Result.error("会话ID不能为空");
            }
            
            List<RecommendationLog> history = recommendationLogService.getLogsBySessionId(sessionId);
            
            log.info("获取推荐历史成功，会话ID: {}, 记录数量: {}", sessionId, history.size());
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取推荐历史失败，会话ID: {}", sessionId, e);
            return Result.error("获取推荐历史失败");
        }
    }

    /**
     * 提交推荐反馈
     */
    @PostMapping("/feedback")
    @Operation(summary = "提交推荐反馈", description = "用户对推荐结果的反馈")
    public Result<String> submitFeedback(
            @Parameter(description = "反馈请求", required = true)
            @RequestBody FeedbackRequest request) {
        try {
            // 验证请求参数
            if (request.getLogId() == null) {
                return Result.error("推荐记录ID不能为空");
            }
            
            if (request.getFeedback() == null) {
                return Result.error("反馈内容不能为空");
            }
            
            // 更新推荐日志的反馈信息
            boolean success = recommendationLogService.updateUserFeedback(
                    request.getLogId(), request.getFeedback(), request.getReason());
            
            if (success) {
                log.info("推荐反馈提交成功，记录ID: {}, 反馈: {}", 
                        request.getLogId(), request.getFeedback());
                return Result.success("感谢您的反馈！");
            } else {
                return Result.error("反馈提交失败");
            }
        } catch (Exception e) {
            log.error("推荐反馈提交失败，记录ID: {}", request.getLogId(), e);
            return Result.error("反馈提交失败");
        }
    }

    /**
     * 转换用户答案格式
     */
    private Map<Long, List<Long>> convertAnswers(List<AnswerItem> answers) {
        return answers.stream()
                .collect(Collectors.toMap(
                        AnswerItem::getQuestionId,
                        AnswerItem::getOptionIds,
                        (existing, replacement) -> replacement
                ));
    }

    /**
     * 构建用户答案详情
     * 
     * @param userAnswers 用户答案映射
     * @return 答案详情
     */
    private Map<String, Object> buildAnswerDetails(Map<Long, List<Long>> userAnswers) {
        Map<String, Object> answerDetails = new HashMap<>();
        List<Map<String, Object>> questionAnswers = new ArrayList<>();
        
        try {
            for (Map.Entry<Long, List<Long>> entry : userAnswers.entrySet()) {
                Long questionId = entry.getKey();
                List<Long> optionIds = entry.getValue();
                
                // 获取问题详情
                Question question = questionService.getById(questionId);
                if (question == null) {
                    continue;
                }
                
                // 获取选项详情
                List<Option> selectedOptions = optionService.getOptionsByIds(optionIds);
                
                Map<String, Object> questionAnswer = new HashMap<>();
                questionAnswer.put("questionId", questionId);
                questionAnswer.put("questionTitle", question.getTitle());
                questionAnswer.put("questionType", question.getQuestionType());
                
                List<Map<String, Object>> optionDetails = selectedOptions.stream()
                        .map(option -> {
                            Map<String, Object> optionMap = new HashMap<>();
                            optionMap.put("optionId", option.getId());
                            optionMap.put("content", option.getContent());
                            optionMap.put("weightValue", option.getWeightAsDouble());
                            optionMap.put("tagKeywords", option.getTagKeywords());
                            return optionMap;
                        })
                        .collect(Collectors.toList());
                
                questionAnswer.put("selectedOptions", optionDetails);
                questionAnswers.add(questionAnswer);
            }
            
            answerDetails.put("questionAnswers", questionAnswers);
            answerDetails.put("totalQuestions", questionAnswers.size());
            
            // 计算总权重
            double totalWeight = questionAnswers.stream()
                    .flatMap(qa -> {
                        Object selectedOptions = qa.get("selectedOptions");
                        if (selectedOptions instanceof List<?>) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> options = (List<Map<String, Object>>) selectedOptions;
                            return options.stream();
                        }
                        return java.util.stream.Stream.<Map<String, Object>>empty();
                    })
                    .mapToDouble(option -> {
                        Object weightValue = option.get("weightValue");
                        return weightValue instanceof Double ? (Double) weightValue : 0.0;
                    })
                    .sum();
            answerDetails.put("totalWeight", totalWeight);
            
        } catch (Exception e) {
            log.error("构建答案详情失败", e);
            answerDetails.put("error", "构建答案详情失败");
        }
        
        return answerDetails;
    }

    /**
     * 保存推荐日志
     */
    private void saveRecommendationLog(RecommendationRequest request,
                                      RecommendationEngine.RecommendationResult result,
                                      HttpServletRequest httpRequest) {
        try {
            RecommendationLog log = new RecommendationLog();
            log.setSessionId(request.getSessionId());
            log.setUserAnswersMap(convertAnswers(request.getAnswers()));
            log.setAlgorithmVersion(result.getAlgorithmVersion());
            log.setTotalScore(java.math.BigDecimal.valueOf(result.getTotalScore()));
            log.setExecutionTimeMs((int) result.getExecutionTimeMs());
            log.setUserIp(getClientIpAddress(httpRequest));
            log.setUserAgent(httpRequest.getHeader("User-Agent"));
            log.setIsTestData(false);
            
            // 设置推荐结果
            List<Map<String, Object>> drinksList = result.getRecommendedDrinks().stream()
                    .map(this::convertRecommendedDrink)
                    .collect(Collectors.toList());
            log.setRecommendedDrinksList(drinksList);
            
            // 设置匹配规则
            List<Map<String, Object>> rulesList = result.getMatchedRules().stream()
                    .map(this::convertMatchedRule)
                    .collect(Collectors.toList());
            log.setMatchedRulesList(rulesList);
            
            recommendationLogService.save(log);
        } catch (Exception e) {
            log.error("保存推荐日志失败", e);
        }
    }

    /**
     * 构建推荐响应数据
     */
    private Map<String, Object> buildRecommendationResponse(RecommendationEngine.RecommendationResult result) {
        Map<String, Object> response = new HashMap<>();
        
        // 推荐酒品列表
        List<Map<String, Object>> recommendations = result.getRecommendedDrinks().stream()
                .map(this::convertRecommendedDrink)
                .collect(Collectors.toList());
        
        response.put("recommendations", recommendations);
        response.put("totalCount", recommendations.size());
        response.put("totalScore", result.getTotalScore());
        response.put("algorithmVersion", result.getAlgorithmVersion());
        response.put("executionTime", result.getExecutionTimeMs());
        
        return response;
    }

    /**
     * 转换推荐酒品数据
     */
    private Map<String, Object> convertRecommendedDrink(RecommendationEngine.RecommendedDrink recommendedDrink) {
        Map<String, Object> drinkMap = new HashMap<>();
        
        Drink drink = recommendedDrink.getDrink();
        drinkMap.put("id", drink.getId());
        drinkMap.put("name", drink.getName());
        drinkMap.put("englishName", drink.getEnglishName());
        drinkMap.put("price", drink.getPrice());
        drinkMap.put("alcoholContent", drink.getAlcoholContent());
        drinkMap.put("description", drink.getDescription());
        drinkMap.put("imageUrl", drink.getImageUrl());
        drinkMap.put("tags", drink.getTags());
        
        drinkMap.put("matchScore", recommendedDrink.getMatchScore());
        drinkMap.put("reason", recommendedDrink.getReason());
        drinkMap.put("matchedTags", recommendedDrink.getMatchedTags());
        
        return drinkMap;
    }

    /**
     * 转换匹配规则数据
     */
    private Map<String, Object> convertMatchedRule(RecommendationEngine.MatchedRule matchedRule) {
        Map<String, Object> ruleMap = new HashMap<>();
        
        RecommendationRule rule = matchedRule.getRule();
        ruleMap.put("id", rule.getId());
        ruleMap.put("ruleName", rule.getRuleName());
        ruleMap.put("conditionType", rule.getConditionType());
        ruleMap.put("matchScore", matchedRule.getMatchScore());
        ruleMap.put("matchedOptionCount", matchedRule.getMatchedOptionCount());
        ruleMap.put("matchedOptionIds", matchedRule.getMatchedOptionIds());
        
        return ruleMap;
    }

    /**
     * 推荐请求类
     */
    public static class RecommendationRequest {
        private String sessionId;
        private List<AnswerItem> answers;
        
        // Getters and Setters
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public List<AnswerItem> getAnswers() { return answers; }
        public void setAnswers(List<AnswerItem> answers) { this.answers = answers; }
    }

    /**
     * 答案项类
     */
    public static class AnswerItem {
        private Long questionId;
        private List<Long> optionIds;
        
        // Getters and Setters
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public List<Long> getOptionIds() { return optionIds; }
        public void setOptionIds(List<Long> optionIds) { this.optionIds = optionIds; }
    }

    /**
     * 反馈请求类
     */
    public static class FeedbackRequest {
        private Long logId;
        private Integer feedback; // 1-满意，0-不满意
        private String reason;
        
        // Getters and Setters
        public Long getLogId() { return logId; }
        public void setLogId(Long logId) { this.logId = logId; }
        public Integer getFeedback() { return feedback; }
        public void setFeedback(Integer feedback) { this.feedback = feedback; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}