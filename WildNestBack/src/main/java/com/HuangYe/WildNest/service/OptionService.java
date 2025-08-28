package com.HuangYe.WildNest.service;

import com.HuangYe.WildNest.entity.Option;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推荐选项服务接口
 * 
 * @author HuangYe
 * @since 2024
 */
public interface OptionService extends IService<Option> {

    /**
     * 根据问题ID获取启用的选项
     * 
     * @param questionId 问题ID
     * @return 选项列表
     */
    List<Option> getActiveOptionsByQuestionId(Long questionId);

    /**
     * 根据问题ID获取所有选项（管理员用）
     * 
     * @param questionId 问题ID
     * @return 选项列表
     */
    List<Option> getAllOptionsByQuestionId(Long questionId);

    /**
     * 分页查询选项（管理员用）
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param questionId 问题ID（可选）
     * @param isActive 是否启用（可选）
     * @return 选项分页数据
     */
    IPage<Option> getOptionsPage(Long current, Long size, Long questionId, Boolean isActive);

    /**
     * 根据选项ID列表获取选项
     * 
     * @param optionIds 选项ID列表
     * @return 选项列表
     */
    List<Option> getOptionsByIds(List<Long> optionIds);

    /**
     * 创建选项
     * 
     * @param option 选项信息
     * @return 创建结果
     */
    boolean createOption(Option option);

    /**
     * 更新选项
     * 
     * @param option 选项信息
     * @return 更新结果
     */
    boolean updateOption(Option option);

    /**
     * 删除选项（软删除）
     * 
     * @param id 选项ID
     * @return 删除结果
     */
    boolean deleteOption(Long id);

    /**
     * 批量更新选项状态
     * 
     * @param ids 选项ID列表
     * @param isActive 是否启用
     * @return 更新结果
     */
    boolean batchUpdateStatus(List<Long> ids, Boolean isActive);

    /**
     * 更新选项排序
     * 
     * @param id 选项ID
     * @param sortOrder 新的排序值
     * @return 更新结果
     */
    boolean updateSortOrder(Long id, Integer sortOrder);

    /**
     * 批量更新选项排序
     * 
     * @param optionIds 选项ID列表
     * @param sortOrders 对应的排序值列表
     * @return 更新结果
     */
    boolean batchUpdateSortOrder(List<Long> optionIds, List<Integer> sortOrders);

    /**
     * 根据标签搜索选项
     * 
     * @param tag 标签关键词
     * @return 选项列表
     */
    List<Option> searchOptionsByTag(String tag);

    /**
     * 根据内容搜索选项
     * 
     * @param content 内容关键词
     * @return 选项列表
     */
    List<Option> searchOptionsByContent(String content);

    /**
     * 根据权重范围查询选项
     * 
     * @param minWeight 最小权重
     * @param maxWeight 最大权重
     * @return 选项列表
     */
    List<Option> getOptionsByWeightRange(BigDecimal minWeight, BigDecimal maxWeight);

    /**
     * 统计问题下启用的选项数量
     * 
     * @param questionId 问题ID
     * @return 选项数量
     */
    Integer countActiveOptionsByQuestionId(Long questionId);

    /**
     * 获取选项统计信息
     * 
     * @return 统计信息
     */
    java.util.Map<String, Object> getOptionStatistics();

    /**
     * 检查问题下排序值是否可用
     * 
     * @param questionId 问题ID
     * @param sortOrder 排序值
     * @param excludeId 排除的选项ID（用于更新时检查）
     * @return 是否可用
     */
    boolean isSortOrderAvailable(Long questionId, Integer sortOrder, Long excludeId);

    /**
     * 获取问题下一个可用的排序值
     * 
     * @param questionId 问题ID
     * @return 排序值
     */
    Integer getNextSortOrder(Long questionId);

    /**
     * 复制问题的所有选项到新问题
     * 
     * @param sourceQuestionId 源问题ID
     * @param targetQuestionId 目标问题ID
     * @return 复制结果
     */
    boolean copyOptionsByQuestionId(Long sourceQuestionId, Long targetQuestionId);

    /**
     * 启用/禁用选项
     * 
     * @param id 选项ID
     * @param isActive 是否启用
     * @return 操作结果
     */
    boolean toggleOptionStatus(Long id, Boolean isActive);

    /**
     * 获取高权重选项
     * 
     * @return 选项列表
     */
    List<Option> getHighWeightOptions();

    /**
     * 获取权重范围统计
     * 
     * @return 统计结果
     */
    List<java.util.Map<String, Object>> getWeightRangeStatistics();

    /**
     * 批量创建选项
     * 
     * @param options 选项列表
     * @return 创建结果
     */
    boolean batchCreateOptions(List<Option> options);

    /**
     * 根据问题ID删除所有选项
     * 
     * @param questionId 问题ID
     * @return 删除结果
     */
    boolean deleteOptionsByQuestionId(Long questionId);
}