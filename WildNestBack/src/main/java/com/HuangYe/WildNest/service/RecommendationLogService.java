package com.HuangYe.WildNest.service;

import com.HuangYe.WildNest.entity.RecommendationLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 推荐日志服务接口
 * 
 * @author HuangYe
 * @since 2024
 */
public interface RecommendationLogService extends IService<RecommendationLog> {

    /**
     * 分页查询推荐日志（管理员用）
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param algorithmVersion 算法版本（可选）
     * @param userFeedback 用户反馈（可选）
     * @param isTestData 是否测试数据（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 日志分页数据
     */
    IPage<RecommendationLog> getLogsPage(Long current, Long size,
                                        String algorithmVersion,
                                        Integer userFeedback,
                                        Boolean isTestData,
                                        LocalDateTime startTime,
                                        LocalDateTime endTime);

    /**
     * 根据会话ID查询推荐日志
     * 
     * @param sessionId 会话ID
     * @return 日志列表
     */
    List<RecommendationLog> getLogsBySessionId(String sessionId);

    /**
     * 根据用户IP查询推荐日志
     * 
     * @param userIp 用户IP
     * @param limit 限制数量
     * @return 日志列表
     */
    List<RecommendationLog> getLogsByUserIp(String userIp, Integer limit);

    /**
     * 查询指定时间范围内的推荐日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    List<RecommendationLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 更新用户反馈
     * 
     * @param id 日志ID
     * @param userFeedback 用户反馈
     * @param feedbackReason 反馈原因
     * @return 更新结果
     */
    boolean updateUserFeedback(Long id, Integer userFeedback, String feedbackReason);

    /**
     * 统计今日推荐次数
     * 
     * @return 推荐次数
     */
    Integer getTodayRecommendationCount();

    /**
     * 统计指定日期的推荐次数
     * 
     * @param date 日期（格式：yyyy-MM-dd）
     * @return 推荐次数
     */
    Integer getRecommendationCountByDate(String date);

    /**
     * 获取用户反馈统计
     * 
     * @return 反馈统计
     */
    List<java.util.Map<String, Object>> getFeedbackStatistics();

    /**
     * 获取算法版本使用统计
     * 
     * @return 版本统计
     */
    List<java.util.Map<String, Object>> getAlgorithmVersionStatistics();

    /**
     * 计算平均推荐分数
     * 
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 平均分数
     */
    BigDecimal getAverageScore(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 计算平均执行时间
     * 
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 平均执行时间（毫秒）
     */
    Double getAverageExecutionTime(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计独立用户数（基于IP）
     * 
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 独立用户数
     */
    Integer getUniqueUserCount(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询满意度高的推荐日志
     * 
     * @param limit 限制数量
     * @return 日志列表
     */
    List<RecommendationLog> getSatisfiedLogs(Integer limit);

    /**
     * 查询不满意的推荐日志
     * 
     * @param limit 限制数量
     * @return 日志列表
     */
    List<RecommendationLog> getUnsatisfiedLogs(Integer limit);

    /**
     * 查询执行时间较长的推荐日志
     * 
     * @param minExecutionTime 最小执行时间（毫秒）
     * @param limit 限制数量
     * @return 日志列表
     */
    List<RecommendationLog> getSlowLogs(Integer minExecutionTime, Integer limit);

    /**
     * 删除指定时间之前的日志
     * 
     * @param beforeTime 时间点
     * @return 删除行数
     */
    Integer deleteLogsBefore(LocalDateTime beforeTime);

    /**
     * 获取推荐服务统计信息
     * 
     * @return 统计信息
     */
    java.util.Map<String, Object> getRecommendationStatistics();

    /**
     * 获取每日推荐数量统计（最近N天）
     * 
     * @param days 天数
     * @return 统计结果
     */
    List<java.util.Map<String, Object>> getDailyRecommendationStats(Integer days);

    /**
     * 获取推荐效果分析
     * 
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 效果分析
     */
    java.util.Map<String, Object> getRecommendationEffectAnalysis(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 创建推荐日志
     * 
     * @param sessionId 会话ID
     * @param userAnswers 用户答案
     * @param recommendedDrinks 推荐结果
     * @param matchedRules 匹配规则
     * @param totalScore 总分数
     * @param executionTime 执行时间
     * @param userIp 用户IP
     * @param userAgent 用户代理
     * @return 创建结果
     */
    boolean createRecommendationLog(String sessionId,
                                   java.util.Map<Long, List<Long>> userAnswers,
                                   List<java.util.Map<String, Object>> recommendedDrinks,
                                   List<java.util.Map<String, Object>> matchedRules,
                                   Double totalScore,
                                   Long executionTime,
                                   String userIp,
                                   String userAgent);

    /**
     * 批量标记测试数据
     * 
     * @param ids 日志ID列表
     * @param isTestData 是否测试数据
     * @return 更新结果
     */
    boolean batchMarkTestData(List<Long> ids, Boolean isTestData);

    /**
     * 清理过期日志
     * 
     * @param retentionDays 保留天数
     * @return 清理结果
     */
    boolean cleanupExpiredLogs(Integer retentionDays);
}