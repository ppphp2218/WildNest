package com.HuangYe.WildNest.mapper;

import com.HuangYe.WildNest.entity.Option;
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
 * 推荐选项Mapper接口
 * 
 * @author HuangYe
 * @since 2024
 */
@Mapper
public interface OptionMapper extends BaseMapper<Option> {

    /**
     * 根据问题ID查询启用的选项，按排序值排序
     * 
     * @param questionId 问题ID
     * @return 选项列表
     */
    @Select("SELECT * FROM `option` WHERE question_id = #{questionId} AND is_active = 1 " +
            "ORDER BY sort_order ASC, created_at ASC")
    List<Option> selectActiveOptionsByQuestionId(@Param("questionId") Long questionId);

    /**
     * 根据问题ID查询所有选项（管理员用）
     * 
     * @param questionId 问题ID
     * @return 选项列表
     */
    @Select("SELECT * FROM `option` WHERE question_id = #{questionId} " +
            "ORDER BY sort_order ASC, created_at ASC")
    List<Option> selectAllOptionsByQuestionId(@Param("questionId") Long questionId);

    /**
     * 分页查询选项（管理员用）
     * 注意：此方法已移除复杂的动态SQL注解，改为在Service层使用QueryWrapper实现
     * 
     * @param page 分页参数
     * @return 选项分页数据
     */
    // 移除动态SQL，改为在Service层使用QueryWrapper
    // IPage<Option> selectOptionsPage(Page<Option> page,
    //                                @Param("questionId") Long questionId,
    //                                @Param("isActive") Boolean isActive);

    /**
     * 根据选项ID列表查询选项
     * 注意：此方法已移除复杂的动态SQL注解，改为在Service层使用QueryWrapper实现
     * 
     * @param optionIds 选项ID列表
     * @return 选项列表
     */
    // 移除动态SQL，改为在Service层使用QueryWrapper
    // List<Option> selectOptionsByIds(@Param("optionIds") List<Long> optionIds);

    /**
     * 根据标签关键词查询选项
     * 
     * @param tag 标签关键词
     * @return 选项列表
     */
    @Select("SELECT * FROM `option` WHERE is_active = 1 " +
            "AND tag_keywords LIKE CONCAT('%', #{tag}, '%') " +
            "ORDER BY weight_value DESC, sort_order ASC")
    List<Option> selectOptionsByTag(@Param("tag") String tag);

    /**
     * 根据权重范围查询选项
     * 
     * @param minWeight 最小权重
     * @param maxWeight 最大权重
     * @return 选项列表
     */
    @Select("SELECT * FROM `option` WHERE is_active = 1 " +
            "AND weight_value >= #{minWeight} AND weight_value <= #{maxWeight} " +
            "ORDER BY weight_value DESC")
    List<Option> selectOptionsByWeightRange(@Param("minWeight") BigDecimal minWeight,
                                           @Param("maxWeight") BigDecimal maxWeight);

    /**
     * 获取问题下选项的最大排序值
     * 
     * @param questionId 问题ID
     * @return 最大排序值
     */
    @Select("SELECT COALESCE(MAX(sort_order), 0) FROM `option` WHERE question_id = #{questionId}")
    Integer getMaxSortOrderByQuestionId(@Param("questionId") Long questionId);

    /**
     * 批量更新选项状态
     * 注意：此方法已移除复杂的动态SQL注解，改为在Service层使用QueryWrapper实现
     * 
     * @param ids 选项ID列表
     * @param isActive 是否启用
     * @return 更新行数
     */
    // 移除动态SQL，改为在Service层使用QueryWrapper
    // int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("isActive") Boolean isActive);

    /**
     * 更新选项排序值
     * 
     * @param id 选项ID
     * @param sortOrder 新的排序值
     * @return 更新行数
     */
    @Update("UPDATE `option` SET sort_order = #{sortOrder} WHERE id = #{id}")
    int updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    /**
     * 统计问题下启用的选项数量
     * 
     * @param questionId 问题ID
     * @return 选项数量
     */
    @Select("SELECT COUNT(*) FROM `option` WHERE question_id = #{questionId} AND is_active = 1")
    Integer countActiveOptionsByQuestionId(@Param("questionId") Long questionId);

    /**
     * 根据内容模糊查询选项
     * 
     * @param content 内容关键词
     * @return 选项列表
     */
    @Select("SELECT * FROM `option` WHERE content LIKE CONCAT('%', #{content}, '%') " +
            "ORDER BY question_id ASC, sort_order ASC")
    List<Option> selectOptionsByContent(@Param("content") String content);

    /**
     * 检查排序值是否已存在
     * 注意：此方法已移除复杂的动态SQL注解，改为在Service层使用QueryWrapper实现
     * 
     * @param questionId 问题ID
     * @param sortOrder 排序值
     * @param excludeId 排除的选项ID（可选）
     * @return 存在的数量
     */
    // 移除动态SQL，改为在Service层使用QueryWrapper
    // Integer checkSortOrderExists(@Param("questionId") Long questionId,
    //                              @Param("sortOrder") Integer sortOrder,
    //                              @Param("excludeId") Long excludeId);

    /**
     * 根据问题ID删除所有选项
     * 
     * @param questionId 问题ID
     * @return 删除行数
     */
    @Select("DELETE FROM `option` WHERE question_id = #{questionId}")
    int deleteByQuestionId(@Param("questionId") Long questionId);

    /**
     * 获取高权重选项（权重值大于1.0）
     * 
     * @return 选项列表
     */
    @Select("SELECT * FROM `option` WHERE is_active = 1 AND weight_value > 1.0 " +
            "ORDER BY weight_value DESC")
    List<Option> selectHighWeightOptions();

    /**
     * 统计各权重范围的选项数量
     * 
     * @return 统计结果
     */
    @Select("SELECT " +
            "CASE " +
            "WHEN weight_value < 0.5 THEN 'low' " +
            "WHEN weight_value >= 0.5 AND weight_value <= 1.5 THEN 'medium' " +
            "ELSE 'high' " +
            "END as weight_range, " +
            "COUNT(*) as count " +
            "FROM `option` WHERE is_active = 1 " +
            "GROUP BY weight_range")
    List<java.util.Map<String, Object>> getWeightRangeStatistics();
}