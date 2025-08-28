package com.HuangYe.WildNest.mapper;

import com.HuangYe.WildNest.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 推荐问题Mapper接口
 * 
 * @author HuangYe
 * @since 2024
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 查询所有启用的问题，按排序值排序
     * 
     * @return 问题列表
     */
    @Select("SELECT * FROM question WHERE is_active = 1 ORDER BY sort_order ASC, created_at ASC")
    List<Question> selectActiveQuestions();

    /**
     * 分页查询问题（管理员用）
     * 注意：此方法已移除复杂的动态SQL注解，改为在Service层使用QueryWrapper实现
     * 
     * @param page 分页参数
     * @return 问题分页数据
     */
    // 移除动态SQL，改为在Service层使用QueryWrapper
    // IPage<Question> selectQuestionsPage(Page<Question> page, 
    //                                    @Param("questionType") String questionType,
    //                                    @Param("isActive") Boolean isActive);

    /**
     * 根据问题类型查询启用的问题
     * 
     * @param questionType 问题类型
     * @return 问题列表
     */
    @Select("SELECT * FROM question WHERE is_active = 1 AND question_type = #{questionType} " +
            "ORDER BY sort_order ASC")
    List<Question> selectActiveQuestionsByType(@Param("questionType") String questionType);

    /**
     * 查询指定排序范围内的问题
     * 
     * @param minOrder 最小排序值
     * @param maxOrder 最大排序值
     * @return 问题列表
     */
    @Select("SELECT * FROM question WHERE is_active = 1 " +
            "AND sort_order >= #{minOrder} AND sort_order <= #{maxOrder} " +
            "ORDER BY sort_order ASC")
    List<Question> selectQuestionsByOrderRange(@Param("minOrder") Integer minOrder,
                                              @Param("maxOrder") Integer maxOrder);

    /**
     * 获取最大排序值
     * 
     * @return 最大排序值
     */
    @Select("SELECT COALESCE(MAX(sort_order), 0) FROM question")
    Integer getMaxSortOrder();

    /**
     * 批量更新问题状态
     * 注意：此方法已移除复杂的动态SQL注解，改为在Service层使用QueryWrapper实现
     * 
     * @param ids 问题ID列表
     * @param isActive 是否启用
     * @return 更新行数
     */
    // 移除动态SQL，改为在Service层使用QueryWrapper
    // int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("isActive") Boolean isActive);

    /**
     * 更新问题排序值
     * 
     * @param id 问题ID
     * @param sortOrder 新的排序值
     * @return 更新行数
     */
    @Update("UPDATE question SET sort_order = #{sortOrder} WHERE id = #{id}")
    int updateSortOrder(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    /**
     * 查询排序值大于指定值的问题
     * 
     * @param sortOrder 排序值
     * @return 问题列表
     */
    @Select("SELECT * FROM question WHERE sort_order > #{sortOrder} ORDER BY sort_order ASC")
    List<Question> selectQuestionsAfterOrder(@Param("sortOrder") Integer sortOrder);

    /**
     * 统计启用的问题数量
     * 
     * @return 问题数量
     */
    @Select("SELECT COUNT(*) FROM question WHERE is_active = 1")
    Integer countActiveQuestions();

    /**
     * 根据标题模糊查询问题
     * 
     * @param title 标题关键词
     * @return 问题列表
     */
    @Select("SELECT * FROM question WHERE title LIKE CONCAT('%', #{title}, '%') " +
            "ORDER BY sort_order ASC")
    List<Question> selectQuestionsByTitle(@Param("title") String title);

    /**
     * 检查排序值是否已存在
     * 注意：此方法已移除复杂的动态SQL注解，改为在Service层使用QueryWrapper实现
     * 
     * @param sortOrder 排序值
     * @param excludeId 排除的问题ID（用于更新时检查）
     * @return 是否存在
     */
    // 移除动态SQL，改为在Service层使用QueryWrapper
    // Integer checkSortOrderExists(@Param("sortOrder") Integer sortOrder, 
    //                              @Param("excludeId") Long excludeId);
}