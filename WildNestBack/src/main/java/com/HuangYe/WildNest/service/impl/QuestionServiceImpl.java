package com.HuangYe.WildNest.service.impl;

import com.HuangYe.WildNest.entity.Question;
import com.HuangYe.WildNest.mapper.QuestionMapper;
import com.HuangYe.WildNest.service.QuestionService;
import com.HuangYe.WildNest.service.OptionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推荐问题服务实现类
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    private final OptionService optionService;

    @Override
    public List<Question> getAllActiveQuestions() {
        try {
            List<Question> questions = baseMapper.selectActiveQuestions();
            
            // 为每个问题加载选项
            for (Question question : questions) {
                question.setOptions(optionService.getActiveOptionsByQuestionId(question.getId()));
            }
            
            log.info("获取启用问题成功，数量: {}", questions.size());
            return questions;
        } catch (Exception e) {
            log.error("获取启用问题失败", e);
            return List.of();
        }
    }

    @Override
    public List<Question> getActiveQuestionsByType(String questionType) {
        if (!StringUtils.hasText(questionType)) {
            return getAllActiveQuestions();
        }
        
        try {
            List<Question> questions = baseMapper.selectActiveQuestionsByType(questionType);
            
            // 为每个问题加载选项
            for (Question question : questions) {
                question.setOptions(optionService.getActiveOptionsByQuestionId(question.getId()));
            }
            
            log.info("根据类型获取问题成功，类型: {}, 数量: {}", questionType, questions.size());
            return questions;
        } catch (Exception e) {
            log.error("根据类型获取问题失败，类型: {}", questionType, e);
            return List.of();
        }
    }

    @Override
    public IPage<Question> getQuestionsPage(Long current, Long size, String questionType, Boolean isActive) {
        try {
            Page<Question> page = new Page<>(current, size);
            
            // 使用LambdaQueryWrapper构建动态查询条件
            LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StringUtils.hasText(questionType), Question::getQuestionType, questionType)
                   .eq(isActive != null, Question::getIsActive, isActive)
                   .orderByAsc(Question::getSortOrder)
                   .orderByDesc(Question::getCreatedAt);
            
            IPage<Question> result = page(page, wrapper);
            
            // 为每个问题加载选项数量
            for (Question question : result.getRecords()) {
                int optionCount = optionService.countActiveOptionsByQuestionId(question.getId());
                // 可以添加一个临时字段存储选项数量
            }
            
            log.info("分页查询问题成功，页码: {}, 大小: {}, 总数: {}", current, size, result.getTotal());
            return result;
        } catch (Exception e) {
            log.error("分页查询问题失败，页码: {}, 大小: {}", current, size, e);
            return new Page<>(current, size);
        }
    }

    @Override
    public Question getQuestionWithOptions(Long id) {
        if (id == null) {
            return null;
        }
        
        try {
            Question question = getById(id);
            if (question != null) {
                question.setOptions(optionService.getAllOptionsByQuestionId(id));
            }
            
            log.info("获取问题详情成功，ID: {}", id);
            return question;
        } catch (Exception e) {
            log.error("获取问题详情失败，ID: {}", id, e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createQuestion(Question question) {
        try {
            // 设置默认值
            if (question.getQuestionType() == null) {
                question.setQuestionType(Question.QuestionType.SINGLE.getCode());
            }
            if (question.getSortOrder() == null) {
                question.setSortOrder(getNextSortOrder());
            }
            if (question.getIsActive() == null) {
                question.setIsActive(true);
            }
            
            boolean result = save(question);
            
            if (result) {
                log.info("创建问题成功，ID: {}, 标题: {}", question.getId(), question.getTitle());
            }
            
            return result;
        } catch (Exception e) {
            log.error("创建问题失败，标题: {}", question.getTitle(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateQuestion(Question question) {
        if (question.getId() == null) {
            return false;
        }
        
        try {
            boolean result = updateById(question);
            
            if (result) {
                log.info("更新问题成功，ID: {}, 标题: {}", question.getId(), question.getTitle());
            }
            
            return result;
        } catch (Exception e) {
            log.error("更新问题失败，ID: {}", question.getId(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQuestion(Long id) {
        if (id == null) {
            return false;
        }
        
        try {
            // 软删除：设置为禁用状态
            Question question = new Question();
            question.setId(id);
            question.setIsActive(false);
            
            boolean result = updateById(question);
            
            if (result) {
                log.info("删除问题成功，ID: {}", id);
            }
            
            return result;
        } catch (Exception e) {
            log.error("删除问题失败，ID: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateStatus(List<Long> ids, Boolean isActive) {
        if (ids == null || ids.isEmpty() || isActive == null) {
            return false;
        }
        
        try {
            // 使用UpdateWrapper替代被移除的batchUpdateStatus方法
            LambdaUpdateWrapper<Question> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(Question::getIsActive, isActive)
                   .in(Question::getId, ids);
            
            boolean result = update(wrapper);
            
            if (result) {
                log.info("批量更新问题状态成功，数量: {}, 状态: {}", ids.size(), isActive);
            }
            
            return result;
        } catch (Exception e) {
            log.error("批量更新问题状态失败，IDs: {}, 状态: {}", ids, isActive, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSortOrder(Long id, Integer sortOrder) {
        if (id == null || sortOrder == null) {
            return false;
        }
        
        try {
            int updatedCount = baseMapper.updateSortOrder(id, sortOrder);
            boolean result = updatedCount > 0;
            
            if (result) {
                log.info("更新问题排序成功，ID: {}, 排序值: {}", id, sortOrder);
            }
            
            return result;
        } catch (Exception e) {
            log.error("更新问题排序失败，ID: {}, 排序值: {}", id, sortOrder, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateSortOrder(List<Long> questionIds, List<Integer> sortOrders) {
        if (questionIds == null || sortOrders == null || 
            questionIds.size() != sortOrders.size()) {
            return false;
        }
        
        try {
            for (int i = 0; i < questionIds.size(); i++) {
                updateSortOrder(questionIds.get(i), sortOrders.get(i));
            }
            
            log.info("批量更新问题排序成功，数量: {}", questionIds.size());
            return true;
        } catch (Exception e) {
            log.error("批量更新问题排序失败", e);
            return false;
        }
    }

    @Override
    public List<Question> searchQuestionsByTitle(String title) {
        if (!StringUtils.hasText(title)) {
            return List.of();
        }
        
        try {
            List<Question> questions = baseMapper.selectQuestionsByTitle(title.trim());
            
            // 为每个问题加载选项
            for (Question question : questions) {
                question.setOptions(optionService.getActiveOptionsByQuestionId(question.getId()));
            }
            
            log.info("搜索问题成功，关键词: {}, 结果数量: {}", title, questions.size());
            return questions;
        } catch (Exception e) {
            log.error("搜索问题失败，关键词: {}", title, e);
            return List.of();
        }
    }

    @Override
    public Map<String, Object> getQuestionStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总问题数
            statistics.put("totalQuestions", count());
            
            // 启用问题数
            Integer activeCount = baseMapper.countActiveQuestions();
            statistics.put("activeQuestions", activeCount);
            
            // 禁用问题数
            statistics.put("inactiveQuestions", count() - activeCount);
            
            // 各类型问题数量统计
            // 这里可以添加更多统计信息
            
            log.info("获取问题统计信息成功");
            return statistics;
        } catch (Exception e) {
            log.error("获取问题统计信息失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public boolean isSortOrderAvailable(Integer sortOrder, Long excludeId) {
        if (sortOrder == null) {
            return false;
        }
        
        try {
            // 使用QueryWrapper替代被移除的checkSortOrderExists方法
            LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Question::getSortOrder, sortOrder);
            if (excludeId != null) {
                wrapper.ne(Question::getId, excludeId);
            }
            
            long count = count(wrapper);
            return count == 0;
        } catch (Exception e) {
            log.error("检查排序值可用性失败，排序值: {}, 排除ID: {}", sortOrder, excludeId, e);
            return false;
        }
    }

    @Override
    public Integer getNextSortOrder() {
        try {
            Integer maxOrder = baseMapper.getMaxSortOrder();
            return maxOrder + 1;
        } catch (Exception e) {
            log.error("获取下一个排序值失败", e);
            return 1;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean copyQuestion(Long id, String newTitle) {
        if (id == null || !StringUtils.hasText(newTitle)) {
            return false;
        }
        
        try {
            Question originalQuestion = getById(id);
            if (originalQuestion == null) {
                return false;
            }
            
            // 创建新问题
            Question newQuestion = new Question();
            newQuestion.setTitle(newTitle);
            newQuestion.setDescription(originalQuestion.getDescription());
            newQuestion.setQuestionType(originalQuestion.getQuestionType());
            newQuestion.setSortOrder(getNextSortOrder());
            newQuestion.setIsActive(true);
            
            boolean result = save(newQuestion);
            
            if (result) {
                // 复制选项
                optionService.copyOptionsByQuestionId(id, newQuestion.getId());
                log.info("复制问题成功，原ID: {}, 新ID: {}, 新标题: {}", id, newQuestion.getId(), newTitle);
            }
            
            return result;
        } catch (Exception e) {
            log.error("复制问题失败，ID: {}, 新标题: {}", id, newTitle, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleQuestionStatus(Long id, Boolean isActive) {
        if (id == null || isActive == null) {
            return false;
        }
        
        try {
            Question question = new Question();
            question.setId(id);
            question.setIsActive(isActive);
            
            boolean result = updateById(question);
            
            if (result) {
                log.info("切换问题状态成功，ID: {}, 状态: {}", id, isActive);
            }
            
            return result;
        } catch (Exception e) {
            log.error("切换问题状态失败，ID: {}, 状态: {}", id, isActive, e);
            return false;
        }
    }
}