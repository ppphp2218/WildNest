package com.HuangYe.WildNest.service.recommendation;

import com.HuangYe.WildNest.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐引擎核心类
 * 负责推荐算法的核心逻辑实现
 * 
 * @author HuangYe
 * @since 2024
 */
@Component
@Slf4j
public class RecommendationEngine {

    /**
     * 推荐结果类
     */
    public static class RecommendationResult {
        private List<RecommendedDrink> recommendedDrinks;
        private List<MatchedRule> matchedRules;
        private double totalScore;
        private long executionTimeMs;
        private String algorithmVersion;

        public RecommendationResult() {
            this.recommendedDrinks = new ArrayList<>();
            this.matchedRules = new ArrayList<>();
            this.algorithmVersion = "v1.0";
        }

        // Getters and Setters
        public List<RecommendedDrink> getRecommendedDrinks() { return recommendedDrinks; }
        public void setRecommendedDrinks(List<RecommendedDrink> recommendedDrinks) { this.recommendedDrinks = recommendedDrinks; }
        public List<MatchedRule> getMatchedRules() { return matchedRules; }
        public void setMatchedRules(List<MatchedRule> matchedRules) { this.matchedRules = matchedRules; }
        public double getTotalScore() { return totalScore; }
        public void setTotalScore(double totalScore) { this.totalScore = totalScore; }
        public long getExecutionTimeMs() { return executionTimeMs; }
        public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
        public String getAlgorithmVersion() { return algorithmVersion; }
        public void setAlgorithmVersion(String algorithmVersion) { this.algorithmVersion = algorithmVersion; }
    }

    /**
     * 推荐酒品类
     */
    public static class RecommendedDrink {
        private Drink drink;
        private double matchScore;
        private String reason;
        private List<String> matchedTags;

        public RecommendedDrink(Drink drink, double matchScore, String reason) {
            this.drink = drink;
            this.matchScore = matchScore;
            this.reason = reason;
            this.matchedTags = new ArrayList<>();
        }

        // Getters and Setters
        public Drink getDrink() { return drink; }
        public void setDrink(Drink drink) { this.drink = drink; }
        public double getMatchScore() { return matchScore; }
        public void setMatchScore(double matchScore) { this.matchScore = matchScore; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public List<String> getMatchedTags() { return matchedTags; }
        public void setMatchedTags(List<String> matchedTags) { this.matchedTags = matchedTags; }
    }

    /**
     * 匹配规则类
     */
    public static class MatchedRule {
        private RecommendationRule rule;
        private double matchScore;
        private int matchedOptionCount;
        private List<Long> matchedOptionIds;

        public MatchedRule(RecommendationRule rule, double matchScore, int matchedOptionCount) {
            this.rule = rule;
            this.matchScore = matchScore;
            this.matchedOptionCount = matchedOptionCount;
            this.matchedOptionIds = new ArrayList<>();
        }

        // Getters and Setters
        public RecommendationRule getRule() { return rule; }
        public void setRule(RecommendationRule rule) { this.rule = rule; }
        public double getMatchScore() { return matchScore; }
        public void setMatchScore(double matchScore) { this.matchScore = matchScore; }
        public int getMatchedOptionCount() { return matchedOptionCount; }
        public void setMatchedOptionCount(int matchedOptionCount) { this.matchedOptionCount = matchedOptionCount; }
        public List<Long> getMatchedOptionIds() { return matchedOptionIds; }
        public void setMatchedOptionIds(List<Long> matchedOptionIds) { this.matchedOptionIds = matchedOptionIds; }
    }

    /**
     * 执行推荐算法
     * 
     * @param userAnswers 用户答案（问题ID -> 选项ID列表）
     * @param allOptions 所有选项
     * @param allRules 所有推荐规则
     * @param allDrinks 所有酒品
     * @return 推荐结果
     */
    public RecommendationResult recommend(Map<Long, List<Long>> userAnswers,
                                        List<Option> allOptions,
                                        List<RecommendationRule> allRules,
                                        List<Drink> allDrinks) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("开始执行推荐算法，用户答案: {}", userAnswers);
            
            RecommendationResult result = new RecommendationResult();
            
            // 1. 获取用户选择的所有选项
            List<Long> selectedOptionIds = userAnswers.values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            
            Map<Long, Option> optionMap = allOptions.stream()
                    .collect(Collectors.toMap(Option::getId, option -> option));
            
            List<Option> selectedOptions = selectedOptionIds.stream()
                    .map(optionMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            
            log.info("用户选择的选项数量: {}", selectedOptions.size());
            
            // 2. 匹配推荐规则
            List<MatchedRule> matchedRules = matchRules(selectedOptionIds, allRules);
            result.setMatchedRules(matchedRules);
            
            log.info("匹配到的规则数量: {}", matchedRules.size());
            
            // 3. 计算酒品推荐分数
            Map<Long, Double> drinkScores = calculateDrinkScores(selectedOptions, matchedRules, allDrinks);
            
            // 4. 生成推荐结果
            List<RecommendedDrink> recommendedDrinks = generateRecommendations(drinkScores, allDrinks, matchedRules, selectedOptions);
            result.setRecommendedDrinks(recommendedDrinks);
            
            // 5. 计算总分数
            double totalScore = recommendedDrinks.stream()
                    .mapToDouble(RecommendedDrink::getMatchScore)
                    .average()
                    .orElse(0.0);
            result.setTotalScore(totalScore);
            
            long endTime = System.currentTimeMillis();
            result.setExecutionTimeMs(endTime - startTime);
            
            log.info("推荐算法执行完成，耗时: {}ms，推荐酒品数量: {}, 总分数: {}", 
                    result.getExecutionTimeMs(), recommendedDrinks.size(), totalScore);
            
            return result;
            
        } catch (Exception e) {
            log.error("推荐算法执行失败", e);
            RecommendationResult result = new RecommendationResult();
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            return result;
        }
    }

    /**
     * 匹配推荐规则
     * 
     * @param selectedOptionIds 用户选择的选项ID列表
     * @param allRules 所有推荐规则
     * @return 匹配的规则列表
     */
    private List<MatchedRule> matchRules(List<Long> selectedOptionIds, List<RecommendationRule> allRules) {
        List<MatchedRule> matchedRules = new ArrayList<>();
        
        for (RecommendationRule rule : allRules) {
            if (!rule.getIsActive()) {
                continue;
            }
            
            List<Long> ruleOptionIds = rule.getOptionCombinationList();
            if (ruleOptionIds.isEmpty()) {
                continue;
            }
            
            // 计算匹配的选项数量
            List<Long> matchedOptionIds = selectedOptionIds.stream()
                    .filter(ruleOptionIds::contains)
                    .collect(Collectors.toList());
            
            int matchedCount = matchedOptionIds.size();
            int totalRuleOptions = ruleOptionIds.size();
            
            // 根据匹配类型判断是否匹配
            boolean isMatched = false;
            double matchScore = 0.0;
            
            String conditionType = rule.getConditionType();
            Integer minMatchCount = rule.getMinMatchCount() != null ? rule.getMinMatchCount() : 1;
            
            switch (conditionType) {
                case "exact":
                    // 精确匹配：必须完全匹配所有选项
                    isMatched = matchedCount == totalRuleOptions && matchedCount == selectedOptionIds.size();
                    matchScore = isMatched ? 100.0 : 0.0;
                    break;
                    
                case "partial":
                    // 部分匹配：匹配数量达到最小要求
                    isMatched = matchedCount >= minMatchCount;
                    matchScore = isMatched ? (double) matchedCount / totalRuleOptions * 100.0 : 0.0;
                    break;
                    
                case "fuzzy":
                    // 模糊匹配：有任何匹配即可
                    isMatched = matchedCount > 0;
                    matchScore = isMatched ? (double) matchedCount / Math.max(totalRuleOptions, selectedOptionIds.size()) * 100.0 : 0.0;
                    break;
                    
                default:
                    log.warn("未知的匹配类型: {}", conditionType);
                    break;
            }
            
            if (isMatched) {
                // 结合规则的匹配分数和优先级
                double finalScore = matchScore * (rule.getMatchScoreAsDouble() / 100.0) * (1 + rule.getPriorityLevel() * 0.1);
                
                MatchedRule matchedRule = new MatchedRule(rule, finalScore, matchedCount);
                matchedRule.setMatchedOptionIds(matchedOptionIds);
                matchedRules.add(matchedRule);
                
                log.debug("规则匹配成功: {}, 匹配分数: {}, 匹配选项数: {}/{}", 
                        rule.getRuleName(), finalScore, matchedCount, totalRuleOptions);
            }
        }
        
        // 按匹配分数排序
        matchedRules.sort((r1, r2) -> Double.compare(r2.getMatchScore(), r1.getMatchScore()));
        
        return matchedRules;
    }

    /**
     * 计算酒品推荐分数
     * 
     * @param selectedOptions 用户选择的选项
     * @param matchedRules 匹配的规则
     * @param allDrinks 所有酒品
     * @return 酒品ID -> 推荐分数的映射
     */
    private Map<Long, Double> calculateDrinkScores(List<Option> selectedOptions,
                                                  List<MatchedRule> matchedRules,
                                                  List<Drink> allDrinks) {
        Map<Long, Double> drinkScores = new HashMap<>();
        
        // 1. 基于规则匹配的分数
        for (MatchedRule matchedRule : matchedRules) {
            List<Long> targetDrinkIds = matchedRule.getRule().getTargetDrinkIdList();
            double ruleScore = matchedRule.getMatchScore();
            
            for (Long drinkId : targetDrinkIds) {
                drinkScores.merge(drinkId, ruleScore, Double::sum);
            }
        }
        
        // 2. 基于标签匹配的分数
        Set<String> userTags = selectedOptions.stream()
                .flatMap(option -> Arrays.stream(option.getTagArray()))
                .collect(Collectors.toSet());
        
        for (Drink drink : allDrinks) {
            if (!drink.getIsAvailable()) {
                continue;
            }
            
            // 计算标签匹配度
            String[] drinkTags = drink.getTags() != null ? drink.getTags().split(",") : new String[0];
            long matchedTagCount = Arrays.stream(drinkTags)
                    .map(String::trim)
                    .filter(userTags::contains)
                    .count();
            
            if (matchedTagCount > 0) {
                double tagScore = (double) matchedTagCount / Math.max(drinkTags.length, userTags.size()) * 50.0;
                drinkScores.merge(drink.getId(), tagScore, Double::sum);
            }
        }
        
        // 3. 基于选项权重的分数
        double totalWeight = selectedOptions.stream()
                .mapToDouble(Option::getWeightAsDouble)
                .sum();
        
        if (totalWeight > 0) {
            double weightBonus = Math.min(totalWeight * 10, 30.0); // 最大30分的权重加成
            
            // 为所有有分数的酒品添加权重加成
            for (Long drinkId : new HashSet<>(drinkScores.keySet())) {
                drinkScores.merge(drinkId, weightBonus, Double::sum);
            }
        }
        
        return drinkScores;
    }

    /**
     * 生成推荐结果
     * 
     * @param drinkScores 酒品分数映射
     * @param allDrinks 所有酒品
     * @param matchedRules 匹配的规则
     * @param selectedOptions 用户选择的选项
     * @return 推荐酒品列表
     */
    private List<RecommendedDrink> generateRecommendations(Map<Long, Double> drinkScores,
                                                          List<Drink> allDrinks,
                                                          List<MatchedRule> matchedRules,
                                                          List<Option> selectedOptions) {
        Map<Long, Drink> drinkMap = allDrinks.stream()
                .collect(Collectors.toMap(Drink::getId, drink -> drink));
        
        List<RecommendedDrink> recommendations = new ArrayList<>();
        
        // 按分数排序，取前10个
        List<Map.Entry<Long, Double>> sortedScores = drinkScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        for (Map.Entry<Long, Double> entry : sortedScores) {
            Long drinkId = entry.getKey();
            Double score = entry.getValue();
            Drink drink = drinkMap.get(drinkId);
            
            if (drink == null || !drink.getIsAvailable()) {
                continue;
            }
            
            // 生成推荐理由
            String reason = generateRecommendationReason(drink, matchedRules, selectedOptions);
            
            // 标准化分数到0-100范围
            double normalizedScore = Math.min(score, 100.0);
            normalizedScore = BigDecimal.valueOf(normalizedScore)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
            
            RecommendedDrink recommendedDrink = new RecommendedDrink(drink, normalizedScore, reason);
            
            // 添加匹配的标签
            Set<String> userTags = selectedOptions.stream()
                    .flatMap(option -> Arrays.stream(option.getTagArray()))
                    .collect(Collectors.toSet());
            
            String[] drinkTags = drink.getTags() != null ? drink.getTags().split(",") : new String[0];
            List<String> matchedTags = Arrays.stream(drinkTags)
                    .map(String::trim)
                    .filter(userTags::contains)
                    .collect(Collectors.toList());
            
            recommendedDrink.setMatchedTags(matchedTags);
            recommendations.add(recommendedDrink);
        }
        
        // 如果没有匹配的酒品，返回默认推荐（热门酒品）
        if (recommendations.isEmpty()) {
            log.info("没有匹配的酒品，返回默认推荐");
            recommendations = getDefaultRecommendations(allDrinks);
        }
        
        return recommendations;
    }

    /**
     * 生成推荐理由
     * 
     * @param drink 酒品
     * @param matchedRules 匹配的规则
     * @param selectedOptions 用户选择的选项
     * @return 推荐理由
     */
    private String generateRecommendationReason(Drink drink,
                                               List<MatchedRule> matchedRules,
                                               List<Option> selectedOptions) {
        // 优先使用匹配规则的推荐理由
        for (MatchedRule matchedRule : matchedRules) {
            List<Long> targetDrinkIds = matchedRule.getRule().getTargetDrinkIdList();
            if (targetDrinkIds.contains(drink.getId())) {
                String ruleReason = matchedRule.getRule().getRecommendationReason();
                if (ruleReason != null && !ruleReason.trim().isEmpty()) {
                    return ruleReason;
                }
            }
        }
        
        // 基于标签生成推荐理由
        Set<String> userTags = selectedOptions.stream()
                .flatMap(option -> Arrays.stream(option.getTagArray()))
                .collect(Collectors.toSet());
        
        String[] drinkTags = drink.getTags() != null ? drink.getTags().split(",") : new String[0];
        List<String> matchedTags = Arrays.stream(drinkTags)
                .map(String::trim)
                .filter(userTags::contains)
                .limit(3)
                .collect(Collectors.toList());
        
        if (!matchedTags.isEmpty()) {
            return String.format("基于您的偏好（%s），这款%s非常适合您", 
                    String.join("、", matchedTags), drink.getName());
        }
        
        // 默认推荐理由
        return String.format("这款%s是我们的热门推荐，相信您会喜欢", drink.getName());
    }

    /**
     * 获取默认推荐（热门酒品）
     * 
     * @param allDrinks 所有酒品
     * @return 默认推荐列表
     */
    private List<RecommendedDrink> getDefaultRecommendations(List<Drink> allDrinks) {
        return allDrinks.stream()
                .filter(drink -> drink.getIsAvailable() && drink.getIsFeatured())
                .sorted((d1, d2) -> Integer.compare(d2.getViewCount(), d1.getViewCount()))
                .limit(5)
                .map(drink -> new RecommendedDrink(drink, 60.0, "热门推荐酒品，深受顾客喜爱"))
                .collect(Collectors.toList());
    }
}