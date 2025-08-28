package com.HuangYe.WildNest.service.impl;

import com.HuangYe.WildNest.entity.RecommendationLog;
import com.HuangYe.WildNest.mapper.RecommendationLogMapper;
import com.HuangYe.WildNest.service.RecommendationLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推荐日志服务实现类
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationLogServiceImpl extends ServiceImpl<RecommendationLogMapper, RecommendationLog> implements RecommendationLogService {

    @Override
    public IPage<RecommendationLog> getLogsPage(Long current, Long size,
                                               String algorithmVersion,
                                               Integer userFeedback,
                                               Boolean isTestData,
                                               LocalDateTime startTime,
                                               LocalDateTime endTime) {
        try {
            Page<RecommendationLog> page = new Page<>(current, size);
            
            // 使用LambdaQueryWrapper构建动态查询条件
            LambdaQueryWrapper<RecommendationLog> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StringUtils.hasText(algorithmVersion), RecommendationLog::getAlgorithmVersion, algorithmVersion)
                   .eq(userFeedback != null, RecommendationLog::getUserFeedback, userFeedback)
                   .eq(isTestData != null, RecommendationLog::getIsTestData, isTestData)
                   .ge(startTime != null, RecommendationLog::getCreatedAt, startTime)
                   .le(endTime != null, RecommendationLog::getCreatedAt, endTime)
                   .orderByDesc(RecommendationLog::getCreatedAt);
            
            IPage<RecommendationLog> result = page(page, wrapper);
            
            log.info("分页查询推荐日志成功，页码: {}, 大小: {}, 总数: {}", current, size, result.getTotal());
            return result;
        } catch (Exception e) {
            log.error("分页查询推荐日志失败，页码: {}, 大小: {}", current, size, e);
            return new Page<>(current, size);
        }
    }

    @Override
    public List<RecommendationLog> getLogsBySessionId(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            return List.of();
        }
        
        try {
            List<RecommendationLog> logs = baseMapper.selectLogsBySessionId(sessionId);
            log.info("根据会话ID查询日志成功，会话ID: {}, 数量: {}", sessionId, logs.size());
            return logs;
        } catch (Exception e) {
            log.error("根据会话ID查询日志失败，会话ID: {}", sessionId, e);
            return List.of();
        }
    }

    @Override
    public List<RecommendationLog> getLogsByUserIp(String userIp, Integer limit) {
        if (!StringUtils.hasText(userIp)) {
            return List.of();
        }
        
        try {
            Integer queryLimit = limit != null ? limit : 10;
            List<RecommendationLog> logs = baseMapper.selectLogsByUserIp(userIp, queryLimit);
            log.info("根据用户IP查询日志成功，IP: {}, 数量: {}", userIp, logs.size());
            return logs;
        } catch (Exception e) {
            log.error("根据用户IP查询日志失败，IP: {}", userIp, e);
            return List.of();
        }
    }

    @Override
    public List<RecommendationLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return List.of();
        }
        
        try {
            List<RecommendationLog> logs = baseMapper.selectLogsByTimeRange(startTime, endTime);
            log.info("根据时间范围查询日志成功，时间范围: {} - {}, 数量: {}", startTime, endTime, logs.size());
            return logs;
        } catch (Exception e) {
            log.error("根据时间范围查询日志失败，时间范围: {} - {}", startTime, endTime, e);
            return List.of();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserFeedback(Long id, Integer userFeedback, String feedbackReason) {
        if (id == null) {
            return false;
        }
        
        try {
            int updatedCount = baseMapper.updateUserFeedback(id, userFeedback, feedbackReason);
            boolean result = updatedCount > 0;
            
            if (result) {
                log.info("更新用户反馈成功，ID: {}, 反馈: {}", id, userFeedback);
            }
            
            return result;
        } catch (Exception e) {
            log.error("更新用户反馈失败，ID: {}, 反馈: {}", id, userFeedback, e);
            return false;
        }
    }

    @Override
    public Integer getTodayRecommendationCount() {
        try {
            Integer count = baseMapper.countTodayRecommendations();
            log.info("获取今日推荐次数成功，次数: {}", count);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("获取今日推荐次数失败", e);
            return 0;
        }
    }

    @Override
    public Integer getRecommendationCountByDate(String date) {
        if (!StringUtils.hasText(date)) {
            return 0;
        }
        
        try {
            Integer count = baseMapper.countRecommendationsByDate(date);
            log.info("获取指定日期推荐次数成功，日期: {}, 次数: {}", date, count);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("获取指定日期推荐次数失败，日期: {}", date, e);
            return 0;
        }
    }

    @Override
    public List<Map<String, Object>> getFeedbackStatistics() {
        try {
            List<Map<String, Object>> statistics = baseMapper.getFeedbackStatistics();
            log.info("获取用户反馈统计成功");
            return statistics;
        } catch (Exception e) {
            log.error("获取用户反馈统计失败", e);
            return List.of();
        }
    }

    @Override
    public List<Map<String, Object>> getAlgorithmVersionStatistics() {
        try {
            List<Map<String, Object>> statistics = baseMapper.getAlgorithmVersionStatistics();
            log.info("获取算法版本统计成功");
            return statistics;
        } catch (Exception e) {
            log.error("获取算法版本统计失败", e);
            return List.of();
        }
    }

    @Override
    public BigDecimal getAverageScore(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            BigDecimal avgScore = baseMapper.getAverageScore(startTime, endTime);
            log.info("计算平均推荐分数成功，分数: {}", avgScore);
            return avgScore != null ? avgScore : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("计算平均推荐分数失败", e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Double getAverageExecutionTime(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            Double avgTime = baseMapper.getAverageExecutionTime(startTime, endTime);
            log.info("计算平均执行时间成功，时间: {}ms", avgTime);
            return avgTime != null ? avgTime : 0.0;
        } catch (Exception e) {
            log.error("计算平均执行时间失败", e);
            return 0.0;
        }
    }

    @Override
    public Integer getUniqueUserCount(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            Integer count = baseMapper.getUniqueUserCount(startTime, endTime);
            log.info("统计独立用户数成功，数量: {}", count);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("统计独立用户数失败", e);
            return 0;
        }
    }

    @Override
    public List<RecommendationLog> getSatisfiedLogs(Integer limit) {
        try {
            Integer queryLimit = limit != null ? limit : 10;
            List<RecommendationLog> logs = baseMapper.selectSatisfiedLogs(queryLimit);
            log.info("查询满意推荐日志成功，数量: {}", logs.size());
            return logs;
        } catch (Exception e) {
            log.error("查询满意推荐日志失败", e);
            return List.of();
        }
    }

    @Override
    public List<RecommendationLog> getUnsatisfiedLogs(Integer limit) {
        try {
            Integer queryLimit = limit != null ? limit : 10;
            List<RecommendationLog> logs = baseMapper.selectUnsatisfiedLogs(queryLimit);
            log.info("查询不满意推荐日志成功，数量: {}", logs.size());
            return logs;
        } catch (Exception e) {
            log.error("查询不满意推荐日志失败", e);
            return List.of();
        }
    }

    @Override
    public List<RecommendationLog> getSlowLogs(Integer minExecutionTime, Integer limit) {
        try {
            Integer queryMinTime = minExecutionTime != null ? minExecutionTime : 1000; // 默认1秒
            Integer queryLimit = limit != null ? limit : 10;
            List<RecommendationLog> logs = baseMapper.selectSlowLogs(queryMinTime, queryLimit);
            log.info("查询慢推荐日志成功，最小执行时间: {}ms, 数量: {}", queryMinTime, logs.size());
            return logs;
        } catch (Exception e) {
            log.error("查询慢推荐日志失败，最小执行时间: {}ms", minExecutionTime, e);
            return List.of();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteLogsBefore(LocalDateTime beforeTime) {
        if (beforeTime == null) {
            return 0;
        }
        
        try {
            int deletedCount = baseMapper.deleteLogsBefore(beforeTime);
            log.info("删除指定时间前的日志成功，时间: {}, 删除数量: {}", beforeTime, deletedCount);
            return deletedCount;
        } catch (Exception e) {
            log.error("删除指定时间前的日志失败，时间: {}", beforeTime, e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getRecommendationStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 基本统计
            statistics.put("totalRecommendations", count());
            statistics.put("todayRecommendations", getTodayRecommendationCount());
            
            // 反馈统计
            List<Map<String, Object>> feedbackStats = getFeedbackStatistics();
            statistics.put("feedbackStats", feedbackStats);
            
            // 算法版本统计
            List<Map<String, Object>> versionStats = getAlgorithmVersionStatistics();
            statistics.put("versionStats", versionStats);
            
            // 平均指标
            statistics.put("averageScore", getAverageScore(null, null));
            statistics.put("averageExecutionTime", getAverageExecutionTime(null, null));
            
            // 独立用户数
            statistics.put("uniqueUsers", getUniqueUserCount(null, null));
            
            // 满意度统计
            long satisfiedCount = lambdaQuery().eq(RecommendationLog::getUserFeedback, 1).count();
            long totalFeedbackCount = lambdaQuery().isNotNull(RecommendationLog::getUserFeedback).count();
            double satisfactionRate = totalFeedbackCount > 0 ? (double) satisfiedCount / totalFeedbackCount * 100 : 0;
            statistics.put("satisfactionRate", satisfactionRate);
            
            log.info("获取推荐服务统计信息成功");
            return statistics;
        } catch (Exception e) {
            log.error("获取推荐服务统计信息失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public List<Map<String, Object>> getDailyRecommendationStats(Integer days) {
        try {
            Integer queryDays = days != null ? days : 7;
            List<Map<String, Object>> statistics = baseMapper.getDailyRecommendationStats(queryDays);
            log.info("获取每日推荐统计成功，天数: {}", queryDays);
            return statistics;
        } catch (Exception e) {
            log.error("获取每日推荐统计失败，天数: {}", days, e);
            return List.of();
        }
    }

    @Override
    public Map<String, Object> getRecommendationEffectAnalysis(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            Map<String, Object> analysis = new HashMap<>();
            
            // 时间范围内的基本指标
            long totalCount = 0;
            long satisfiedCount = 0;
            long feedbackCount = 0;
            
            if (startTime != null && endTime != null) {
                totalCount = lambdaQuery()
                        .between(RecommendationLog::getCreatedAt, startTime, endTime)
                        .eq(RecommendationLog::getIsTestData, false)
                        .count();
                        
                satisfiedCount = lambdaQuery()
                        .between(RecommendationLog::getCreatedAt, startTime, endTime)
                        .eq(RecommendationLog::getUserFeedback, 1)
                        .eq(RecommendationLog::getIsTestData, false)
                        .count();
                        
                feedbackCount = lambdaQuery()
                        .between(RecommendationLog::getCreatedAt, startTime, endTime)
                        .isNotNull(RecommendationLog::getUserFeedback)
                        .eq(RecommendationLog::getIsTestData, false)
                        .count();
            } else {
                totalCount = lambdaQuery().eq(RecommendationLog::getIsTestData, false).count();
                satisfiedCount = lambdaQuery()
                        .eq(RecommendationLog::getUserFeedback, 1)
                        .eq(RecommendationLog::getIsTestData, false)
                        .count();
                feedbackCount = lambdaQuery()
                        .isNotNull(RecommendationLog::getUserFeedback)
                        .eq(RecommendationLog::getIsTestData, false)
                        .count();
            }
            
            // 计算各种率
            double satisfactionRate = feedbackCount > 0 ? (double) satisfiedCount / feedbackCount * 100 : 0;
            double feedbackRate = totalCount > 0 ? (double) feedbackCount / totalCount * 100 : 0;
            
            analysis.put("totalRecommendations", totalCount);
            analysis.put("satisfiedRecommendations", satisfiedCount);
            analysis.put("feedbackRecommendations", feedbackCount);
            analysis.put("satisfactionRate", satisfactionRate);
            analysis.put("feedbackRate", feedbackRate);
            
            // 平均指标
            analysis.put("averageScore", getAverageScore(startTime, endTime));
            analysis.put("averageExecutionTime", getAverageExecutionTime(startTime, endTime));
            analysis.put("uniqueUsers", getUniqueUserCount(startTime, endTime));
            
            // 时间范围
            analysis.put("analysisStartTime", startTime);
            analysis.put("analysisEndTime", endTime);
            
            log.info("获取推荐效果分析成功");
            return analysis;
        } catch (Exception e) {
            log.error("获取推荐效果分析失败", e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRecommendationLog(String sessionId,
                                          Map<Long, List<Long>> userAnswers,
                                          List<Map<String, Object>> recommendedDrinks,
                                          List<Map<String, Object>> matchedRules,
                                          Double totalScore,
                                          Long executionTime,
                                          String userIp,
                                          String userAgent) {
        try {
            RecommendationLog recommendationLog = new RecommendationLog();
            recommendationLog.setSessionId(sessionId);
            recommendationLog.setUserAnswersMap(userAnswers);
            recommendationLog.setRecommendedDrinksList(recommendedDrinks);
            recommendationLog.setMatchedRulesList(matchedRules);
            recommendationLog.setTotalScore(totalScore != null ? BigDecimal.valueOf(totalScore) : null);
            recommendationLog.setExecutionTimeMs(executionTime != null ? executionTime.intValue() : null);
            recommendationLog.setUserIp(userIp);
            recommendationLog.setUserAgent(userAgent);
            recommendationLog.setAlgorithmVersion("v1.0");
            recommendationLog.setIsTestData(false);
            
            boolean result = save(recommendationLog);
            
            if (result) {
                log.info("创建推荐日志成功，会话ID: {}", sessionId);
            }
            
            return result;
        } catch (Exception e) {
            log.error("创建推荐日志失败，会话ID: {}", sessionId, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchMarkTestData(List<Long> ids, Boolean isTestData) {
        if (ids == null || ids.isEmpty() || isTestData == null) {
            return false;
        }
        
        try {
            boolean result = lambdaUpdate()
                    .in(RecommendationLog::getId, ids)
                    .set(RecommendationLog::getIsTestData, isTestData)
                    .update();
            
            if (result) {
                log.info("批量标记测试数据成功，数量: {}, 标记: {}", ids.size(), isTestData);
            }
            
            return result;
        } catch (Exception e) {
            log.error("批量标记测试数据失败，IDs: {}, 标记: {}", ids, isTestData, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cleanupExpiredLogs(Integer retentionDays) {
        try {
            Integer days = retentionDays != null ? retentionDays : 90; // 默认保留90天
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(days);
            
            Integer deletedCount = deleteLogsBefore(cutoffTime);
            
            log.info("清理过期日志完成，保留天数: {}, 删除数量: {}", days, deletedCount);
            return true;
        } catch (Exception e) {
            log.error("清理过期日志失败，保留天数: {}", retentionDays, e);
            return false;
        }
    }
}