package com.HuangYe.WildNest.service;

import com.HuangYe.WildNest.entity.Question;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 推荐问题服务接口
 * 
 * @author HuangYe
 * @since 2024
 */
public interface QuestionService extends IService<Question> {

    /**
     * 获取所有启用的问题（包含选项）
     * 
     * @return 问题列表
     */
    List<Question> getAllActiveQuestions();

    /**
     * 根据问题类型获取启用的问题
     * 
     * @param questionType 问题类型
     * @return 问题列表
     */
    List<Question> getActiveQuestionsByType(String questionType);

    /**
     * 分页查询问题（管理员用）
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param questionType 问题类型（可选）
     * @param isActive 是否启用（可选）
     * @return 问题分页数据
     */
    IPage<Question> getQuestionsPage(Long current, Long size, String questionType, Boolean isActive);

    /**
     * 根据ID获取问题详情（包含选项）
     * 
     * @param id 问题ID
     * @return 问题详情
     */
    Question getQuestionWithOptions(Long id);

    /**
     * 创建问题
     * 
     * @param question 问题信息
     * @return 创建结果
     */
    boolean createQuestion(Question question);

    /**
     * 更新问题
     * 
     * @param question 问题信息
     * @return 更新结果
     */
    boolean updateQuestion(Question question);

    /**
     * 删除问题（软删除）
     * 
     * @param id 问题ID
     * @return 删除结果
     */
    boolean deleteQuestion(Long id);

    /**
     * 批量更新问题状态
     * 
     * @param ids 问题ID列表
     * @param isActive 是否启用
     * @return 更新结果
     */
    boolean batchUpdateStatus(List<Long> ids, Boolean isActive);

    /**
     * 更新问题排序
     * 
     * @param id 问题ID
     * @param sortOrder 新的排序值
     * @return 更新结果
     */
    boolean updateSortOrder(Long id, Integer sortOrder);

    /**
     * 批量更新问题排序
     * 
     * @param questionIds 问题ID列表
     * @param sortOrders 对应的排序值列表
     * @return 更新结果
     */
    boolean batchUpdateSortOrder(List<Long> questionIds, List<Integer> sortOrders);

    /**
     * 根据标题搜索问题
     * 
     * @param title 标题关键词
     * @return 问题列表
     */
    List<Question> searchQuestionsByTitle(String title);

    /**
     * 获取问题统计信息
     * 
     * @return 统计信息
     */
    java.util.Map<String, Object> getQuestionStatistics();

    /**
     * 检查排序值是否可用
     * 
     * @param sortOrder 排序值
     * @param excludeId 排除的问题ID（用于更新时检查）
     * @return 是否可用
     */
    boolean isSortOrderAvailable(Integer sortOrder, Long excludeId);

    /**
     * 获取下一个可用的排序值
     * 
     * @return 排序值
     */
    Integer getNextSortOrder();

    /**
     * 复制问题
     * 
     * @param id 原问题ID
     * @param newTitle 新问题标题
     * @return 复制结果
     */
    boolean copyQuestion(Long id, String newTitle);

    /**
     * 启用/禁用问题
     * 
     * @param id 问题ID
     * @param isActive 是否启用
     * @return 操作结果
     */
    boolean toggleQuestionStatus(Long id, Boolean isActive);
}