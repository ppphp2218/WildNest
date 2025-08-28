package com.HuangYe.WildNest.mapper;

import com.HuangYe.WildNest.entity.RecommendationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 推荐日志Mapper接口
 * 
 * @author HuangYe
 * @since 2024
 */
@Mapper
public interface RecommendationLogMapper extends BaseMapper<RecommendationLog> {

    /**
     * 分页查询推荐日志（管理员用）
     * 注意：此方法已移除复杂的动态SQL注解，改为在Service层使用QueryWrapper实现
     * 
     * @param page 分页参数
     * @return 日志分页数据
     */
    // 移除动态SQL，改为在Service层使用QueryWrapper
    // IPage<RecommendationLog> selectLogsPage(Page<RecommendationLog> page,
    //                                        @Param("algorithmVersion") String algorithmVersion,
    //                                        @Param("userFeedback") Integer userFeedback,
    //                                        @Param("isTestData") Boolean isTestData,
    //                                        @Param("startTime") LocalDateTime startTime,
    //                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 根据会话ID查询推荐日志
     * 
     * @param sessionId 会话ID
     * @return 日志列表
     */
    @Select("SELECT * FROM recommendation_log WHERE session_id = #{sessionId} " +
            "ORDER BY created_at DESC")
    List<RecommendationLog> selectLogsBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据用户IP查询推荐日志
     * 
     * @param userIp 用户IP
     * @param limit 限制数量
     * @return 日志列表
     */
    @Select("SELECT * FROM recommendation_log WHERE user_ip = #{userIp} " +
            "ORDER BY created_at DESC LIMIT #{limit}")
    List<RecommendationLog> selectLogsByUserIp(@Param("userIp") String userIp,
                                              @Param("limit") Integer limit);

    /**
     * 查询指定时间范围内的推荐日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 日志列表
     */
    @Select("SELECT * FROM recommendation_log WHERE created_at >= #{startTime} " +
            "AND created_at <= #{endTime} ORDER BY created_at DESC")
    List<RecommendationLog> selectLogsByTimeRange(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 统计今日推荐次数
     * 
     * @return 推荐次数
     */
    @Select("SELECT COUNT(*) FROM recommendation_log WHERE DATE(created_at) = CURDATE() " +
            "AND is_test_data = 0")
    Integer countTodayRecommendations();

    /**
     * 统计指定日期的推荐次数
     * 
     * @param date 日期
     * @return 推荐次数
     */
    @Select("SELECT COUNT(*) FROM recommendation_log WHERE DATE(created_at) = #{date} " +
            "AND is_test_data = 0")
    Integer countRecommendationsByDate(@Param("date") String date);

    /**
     * 统计用户反馈情况
     * 
     * @return 反馈统计
     */
    @Select("SELECT " +
            "user_feedback, " +
            "COUNT(*) as count " +
            "FROM recommendation_log " +
            "WHERE user_feedback IS NOT NULL AND is_test_data = 0 " +
            "GROUP BY user_feedback")
    List<java.util.Map<String, Object>> getFeedbackStatistics();

    /**
     * 统计算法版本使用情况
     * 
     * @return 版本统计
     */
    @Select("SELECT " +
            "algorithm_version, " +
            "COUNT(*) as count " +
            "FROM recommendation_log " +
            "WHERE is_test_data = 0 " +
            "GROUP BY algorithm_version " +
            "ORDER BY count DESC")
    List<java.util.Map<String, Object>> getAlgorithmVersionStatistics();

    /**
     * 计算平均推荐分数
     * 
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 平均分数
     */
    @Select("SELECT AVG(total_score) FROM recommendation_log " +
            "WHERE total_score IS NOT NULL AND is_test_data = 0")
    BigDecimal getAverageScore(@Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime);

    /**
     * 计算平均执行时间
     * 
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 平均执行时间（毫秒）
     */
    @Select("SELECT AVG(execution_time_ms) FROM recommendation_log " +
            "WHERE execution_time_ms IS NOT NULL AND is_test_data = 0")
    Double getAverageExecutionTime(@Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 统计独立用户数（基于IP）
     * 
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 独立用户数
     */
    @Select("SELECT COUNT(DISTINCT user_ip) FROM recommendation_log " +
            "WHERE user_ip IS NOT NULL AND is_test_data = 0")
    Integer getUniqueUserCount(@Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime);

    /**
     * 更新用户反馈
     * 
     * @param id 日志ID
     * @param userFeedback 用户反馈
     * @param feedbackReason 反馈原因
     * @return 更新行数
     */
    @Update("UPDATE recommendation_log SET user_feedback = #{userFeedback}, " +
            "feedback_reason = #{feedbackReason} WHERE id = #{id}")
    int updateUserFeedback(@Param("id") Long id,
                          @Param("userFeedback") Integer userFeedback,
                          @Param("feedbackReason") String feedbackReason);

    /**
     * 查询满意度高的推荐日志
     * 
     * @param limit 限制数量
     * @return 日志列表
     */
    @Select("SELECT * FROM recommendation_log WHERE user_feedback = 1 AND is_test_data = 0 " +
            "ORDER BY total_score DESC, created_at DESC LIMIT #{limit}")
    List<RecommendationLog> selectSatisfiedLogs(@Param("limit") Integer limit);

    /**
     * 查询不满意的推荐日志
     * 
     * @param limit 限制数量
     * @return 日志列表
     */
    @Select("SELECT * FROM recommendation_log WHERE user_feedback = 0 AND is_test_data = 0 " +
            "ORDER BY created_at DESC LIMIT #{limit}")
    List<RecommendationLog> selectUnsatisfiedLogs(@Param("limit") Integer limit);

    /**
     * 删除指定时间之前的日志
     * 
     * @param beforeTime 时间点
     * @return 删除数量
     */
    @Update("DELETE FROM recommendation_log WHERE created_at < #{beforeTime}")
    int deleteLogsBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 查询执行时间较长的推荐日志
     * 
     * @param minExecutionTime 最小执行时间（毫秒）
     * @param limit 限制数量
     * @return 日志列表
     */
    @Select("SELECT * FROM recommendation_log WHERE execution_time_ms >= #{minExecutionTime} " +
            "AND is_test_data = 0 ORDER BY execution_time_ms DESC LIMIT #{limit}")
    List<RecommendationLog> selectSlowLogs(@Param("minExecutionTime") Integer minExecutionTime,
                                          @Param("limit") Integer limit);

    /**
     * 统计每日推荐数量（最近N天）
     * 
     * @param days 天数
     * @return 统计结果
     */
    @Select("SELECT " +
            "DATE(created_at) as date, " +
            "COUNT(*) as count " +
            "FROM recommendation_log " +
            "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "AND is_test_data = 0 " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date DESC")
    List<java.util.Map<String, Object>> getDailyRecommendationStats(@Param("days") Integer days);
}