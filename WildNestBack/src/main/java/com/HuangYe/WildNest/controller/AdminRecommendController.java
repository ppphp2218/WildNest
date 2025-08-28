package com.HuangYe.WildNest.controller;

import com.HuangYe.WildNest.common.Result;
import com.HuangYe.WildNest.entity.*;
import com.HuangYe.WildNest.service.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 推荐服务管理员控制器
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@RestController
@RequestMapping("/admin/recommend")
@RequiredArgsConstructor
@Validated
@Tag(name = "推荐服务管理", description = "推荐服务管理员接口")
public class AdminRecommendController extends BaseController {

    private final QuestionService questionService;
    private final OptionService optionService;
    private final RecommendationRuleService recommendationRuleService;
    private final RecommendationLogService recommendationLogService;

    // ====================================
    // 问题管理接口
    // ====================================

    /**
     * 分页查询问题列表
     */
    @GetMapping("/questions")
    @Operation(summary = "分页查询问题", description = "管理员分页查询推荐问题")
    public Result<IPage<Question>> getQuestionsPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "问题类型") @RequestParam(required = false) String questionType,
            @Parameter(description = "是否启用") @RequestParam(required = false) Boolean isActive) {
        try {
            IPage<Question> page = questionService.getQuestionsPage(current, size, questionType, isActive);
            log.info("管理员查询问题列表成功，页码: {}, 大小: {}, 总数: {}", current, size, page.getTotal());
            return Result.success(page);
        } catch (Exception e) {
            log.error("管理员查询问题列表失败", e);
            return Result.error("查询问题列表失败");
        }
    }

    /**
     * 获取问题详情
     */
    @GetMapping("/questions/{id}")
    @Operation(summary = "获取问题详情", description = "根据ID获取问题详情")
    public Result<Question> getQuestionDetail(@PathVariable Long id) {
        try {
            Question question = questionService.getQuestionWithOptions(id);
            if (question == null) {
                return Result.error("问题不存在");
            }
            log.info("获取问题详情成功，ID: {}", id);
            return Result.success(question);
        } catch (Exception e) {
            log.error("获取问题详情失败，ID: {}", id, e);
            return Result.error("获取问题详情失败");
        }
    }

    /**
     * 创建问题
     */
    @PostMapping("/questions")
    @Operation(summary = "创建问题", description = "创建新的推荐问题")
    public Result<String> createQuestion(@RequestBody Question question) {
        try {
            boolean success = questionService.createQuestion(question);
            if (success) {
                log.info("创建问题成功，标题: {}", question.getTitle());
                return Result.success("创建问题成功");
            } else {
                return Result.error("创建问题失败");
            }
        } catch (Exception e) {
            log.error("创建问题失败", e);
            return Result.error("创建问题失败");
        }
    }

    /**
     * 更新问题
     */
    @PutMapping("/questions/{id}")
    @Operation(summary = "更新问题", description = "更新推荐问题信息")
    public Result<String> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        try {
            question.setId(id);
            boolean success = questionService.updateQuestion(question);
            if (success) {
                log.info("更新问题成功，ID: {}", id);
                return Result.success("更新问题成功");
            } else {
                return Result.error("更新问题失败");
            }
        } catch (Exception e) {
            log.error("更新问题失败，ID: {}", id, e);
            return Result.error("更新问题失败");
        }
    }

    /**
     * 删除问题
     */
    @DeleteMapping("/questions/{id}")
    @Operation(summary = "删除问题", description = "删除推荐问题")
    public Result<String> deleteQuestion(@PathVariable Long id) {
        try {
            boolean success = questionService.deleteQuestion(id);
            if (success) {
                log.info("删除问题成功，ID: {}", id);
                return Result.success("删除问题成功");
            } else {
                return Result.error("删除问题失败");
            }
        } catch (Exception e) {
            log.error("删除问题失败，ID: {}", id, e);
            return Result.error("删除问题失败");
        }
    }

    /**
     * 批量更新问题状态
     */
    @PutMapping("/questions/status")
    @Operation(summary = "批量更新问题状态", description = "批量启用或禁用问题")
    public Result<String> batchUpdateQuestionStatus(
            @RequestBody BatchUpdateRequest request) {
        try {
            boolean success = questionService.batchUpdateStatus(request.getIds(), request.getIsActive());
            if (success) {
                log.info("批量更新问题状态成功，数量: {}, 状态: {}", request.getIds().size(), request.getIsActive());
                return Result.success("批量更新状态成功");
            } else {
                return Result.error("批量更新状态失败");
            }
        } catch (Exception e) {
            log.error("批量更新问题状态失败", e);
            return Result.error("批量更新状态失败");
        }
    }

    // ====================================
    // 选项管理接口
    // ====================================

    /**
     * 分页查询选项列表
     */
    @GetMapping("/options")
    @Operation(summary = "分页查询选项", description = "管理员分页查询推荐选项")
    public Result<IPage<Option>> getOptionsPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "问题ID") @RequestParam(required = false) Long questionId,
            @Parameter(description = "是否启用") @RequestParam(required = false) Boolean isActive) {
        try {
            IPage<Option> page = optionService.getOptionsPage(current, size, questionId, isActive);
            log.info("管理员查询选项列表成功，页码: {}, 大小: {}, 总数: {}", current, size, page.getTotal());
            return Result.success(page);
        } catch (Exception e) {
            log.error("管理员查询选项列表失败", e);
            return Result.error("查询选项列表失败");
        }
    }

    /**
     * 创建选项
     */
    @PostMapping("/options")
    @Operation(summary = "创建选项", description = "创建新的推荐选项")
    public Result<String> createOption(@RequestBody Option option) {
        try {
            boolean success = optionService.createOption(option);
            if (success) {
                log.info("创建选项成功，内容: {}", option.getContent());
                return Result.success("创建选项成功");
            } else {
                return Result.error("创建选项失败");
            }
        } catch (Exception e) {
            log.error("创建选项失败", e);
            return Result.error("创建选项失败");
        }
    }

    /**
     * 更新选项
     */
    @PutMapping("/options/{id}")
    @Operation(summary = "更新选项", description = "更新推荐选项信息")
    public Result<String> updateOption(@PathVariable Long id, @RequestBody Option option) {
        try {
            option.setId(id);
            boolean success = optionService.updateOption(option);
            if (success) {
                log.info("更新选项成功，ID: {}", id);
                return Result.success("更新选项成功");
            } else {
                return Result.error("更新选项失败");
            }
        } catch (Exception e) {
            log.error("更新选项失败，ID: {}", id, e);
            return Result.error("更新选项失败");
        }
    }

    /**
     * 删除选项
     */
    @DeleteMapping("/options/{id}")
    @Operation(summary = "删除选项", description = "删除推荐选项")
    public Result<String> deleteOption(@PathVariable Long id) {
        try {
            boolean success = optionService.deleteOption(id);
            if (success) {
                log.info("删除选项成功，ID: {}", id);
                return Result.success("删除选项成功");
            } else {
                return Result.error("删除选项失败");
            }
        } catch (Exception e) {
            log.error("删除选项失败，ID: {}", id, e);
            return Result.error("删除选项失败");
        }
    }

    /**
     * 批量更新选项状态
     */
    @PutMapping("/options/status")
    @Operation(summary = "批量更新选项状态", description = "批量启用或禁用选项")
    public Result<String> batchUpdateOptionStatus(
            @RequestBody BatchUpdateRequest request) {
        try {
            boolean success = optionService.batchUpdateStatus(request.getIds(), request.getIsActive());
            if (success) {
                log.info("批量更新选项状态成功，数量: {}, 状态: {}", request.getIds().size(), request.getIsActive());
                return Result.success("批量更新状态成功");
            } else {
                return Result.error("批量更新状态失败");
            }
        } catch (Exception e) {
            log.error("批量更新选项状态失败", e);
            return Result.error("批量更新状态失败");
        }
    }

    // ====================================
    // 推荐规则管理接口
    // ====================================

    /**
     * 分页查询推荐规则
     */
    @GetMapping("/rules")
    @Operation(summary = "分页查询推荐规则", description = "管理员分页查询推荐规则")
    public Result<IPage<RecommendationRule>> getRulesPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "匹配类型") @RequestParam(required = false) String conditionType,
            @Parameter(description = "是否启用") @RequestParam(required = false) Boolean isActive) {
        try {
            IPage<RecommendationRule> page = recommendationRuleService.getRulesPage(current, size, conditionType, isActive);
            log.info("管理员查询推荐规则列表成功，页码: {}, 大小: {}, 总数: {}", current, size, page.getTotal());
            return Result.success(page);
        } catch (Exception e) {
            log.error("管理员查询推荐规则列表失败", e);
            return Result.error("查询推荐规则列表失败");
        }
    }

    /**
     * 获取推荐规则详情
     */
    @GetMapping("/rules/{id}")
    @Operation(summary = "获取推荐规则详情", description = "根据ID获取推荐规则详情")
    public Result<RecommendationRule> getRuleDetail(@PathVariable Long id) {
        try {
            RecommendationRule rule = recommendationRuleService.getById(id);
            if (rule == null) {
                return Result.error("推荐规则不存在");
            }
            log.info("获取推荐规则详情成功，ID: {}", id);
            return Result.success(rule);
        } catch (Exception e) {
            log.error("获取推荐规则详情失败，ID: {}", id, e);
            return Result.error("获取推荐规则详情失败");
        }
    }

    /**
     * 创建推荐规则
     */
    @PostMapping("/rules")
    @Operation(summary = "创建推荐规则", description = "创建新的推荐规则")
    public Result<String> createRule(@RequestBody RecommendationRule rule) {
        try {
            boolean success = recommendationRuleService.createRule(rule);
            if (success) {
                log.info("创建推荐规则成功，名称: {}", rule.getRuleName());
                return Result.success("创建推荐规则成功");
            } else {
                return Result.error("创建推荐规则失败");
            }
        } catch (Exception e) {
            log.error("创建推荐规则失败", e);
            return Result.error("创建推荐规则失败");
        }
    }

    /**
     * 更新推荐规则
     */
    @PutMapping("/rules/{id}")
    @Operation(summary = "更新推荐规则", description = "更新推荐规则信息")
    public Result<String> updateRule(@PathVariable Long id, @RequestBody RecommendationRule rule) {
        try {
            rule.setId(id);
            boolean success = recommendationRuleService.updateRule(rule);
            if (success) {
                log.info("更新推荐规则成功，ID: {}", id);
                return Result.success("更新推荐规则成功");
            } else {
                return Result.error("更新推荐规则失败");
            }
        } catch (Exception e) {
            log.error("更新推荐规则失败，ID: {}", id, e);
            return Result.error("更新推荐规则失败");
        }
    }

    /**
     * 删除推荐规则
     */
    @DeleteMapping("/rules/{id}")
    @Operation(summary = "删除推荐规则", description = "删除推荐规则")
    public Result<String> deleteRule(@PathVariable Long id) {
        try {
            boolean success = recommendationRuleService.deleteRule(id);
            if (success) {
                log.info("删除推荐规则成功，ID: {}", id);
                return Result.success("删除推荐规则成功");
            } else {
                return Result.error("删除推荐规则失败");
            }
        } catch (Exception e) {
            log.error("删除推荐规则失败，ID: {}", id, e);
            return Result.error("删除推荐规则失败");
        }
    }

    // ====================================
    // 推荐数据统计接口
    // ====================================

    /**
     * 获取推荐服务统计概览
     */
    @GetMapping("/statistics/overview")
    @Operation(summary = "获取推荐统计概览", description = "获取推荐服务的整体统计数据")
    public Result<Map<String, Object>> getStatisticsOverview() {
        try {
            Map<String, Object> statistics = recommendationLogService.getRecommendationStatistics();
            log.info("获取推荐统计概览成功");
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取推荐统计概览失败", e);
            return Result.error("获取推荐统计概览失败");
        }
    }

    /**
     * 获取每日推荐数量统计
     */
    @GetMapping("/statistics/daily")
    @Operation(summary = "获取每日推荐统计", description = "获取最近N天的推荐数量统计")
    public Result<List<Map<String, Object>>> getDailyStatistics(
            @Parameter(description = "统计天数") @RequestParam(defaultValue = "7") Integer days) {
        try {
            List<Map<String, Object>> statistics = recommendationLogService.getDailyRecommendationStats(days);
            log.info("获取每日推荐统计成功，天数: {}", days);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取每日推荐统计失败，天数: {}", days, e);
            return Result.error("获取每日推荐统计失败");
        }
    }

    /**
     * 获取用户反馈统计
     */
    @GetMapping("/statistics/feedback")
    @Operation(summary = "获取用户反馈统计", description = "获取用户反馈的统计数据")
    public Result<List<Map<String, Object>>> getFeedbackStatistics() {
        try {
            List<Map<String, Object>> statistics = recommendationLogService.getFeedbackStatistics();
            log.info("获取用户反馈统计成功");
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取用户反馈统计失败", e);
            return Result.error("获取用户反馈统计失败");
        }
    }

    // ====================================
    // 推荐日志查询接口
    // ====================================

    /**
     * 分页查询推荐日志
     */
    @GetMapping("/logs")
    @Operation(summary = "分页查询推荐日志", description = "管理员分页查询推荐日志")
    public Result<IPage<RecommendationLog>> getLogsPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "算法版本") @RequestParam(required = false) String algorithmVersion,
            @Parameter(description = "用户反馈") @RequestParam(required = false) Integer userFeedback,
            @Parameter(description = "是否测试数据") @RequestParam(required = false) Boolean isTestData,
            @Parameter(description = "开始时间") @RequestParam(required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) LocalDateTime endTime) {
        try {
            IPage<RecommendationLog> page = recommendationLogService.getLogsPage(
                    current, size, algorithmVersion, userFeedback, isTestData, startTime, endTime);
            log.info("管理员查询推荐日志成功，页码: {}, 大小: {}, 总数: {}", current, size, page.getTotal());
            return Result.success(page);
        } catch (Exception e) {
            log.error("管理员查询推荐日志失败", e);
            return Result.error("查询推荐日志失败");
        }
    }

    // ====================================
    // 推荐效果分析接口
    // ====================================

    /**
     * 获取推荐效果分析
     */
    @GetMapping("/analysis/effect")
    @Operation(summary = "获取推荐效果分析", description = "分析推荐算法的效果")
    public Result<Map<String, Object>> getRecommendationEffectAnalysis(
            @Parameter(description = "开始时间") @RequestParam(required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) LocalDateTime endTime) {
        try {
            Map<String, Object> analysis = recommendationLogService.getRecommendationEffectAnalysis(startTime, endTime);
            log.info("获取推荐效果分析成功");
            return Result.success(analysis);
        } catch (Exception e) {
            log.error("获取推荐效果分析失败", e);
            return Result.error("获取推荐效果分析失败");
        }
    }

    // ====================================
    // 请求对象定义
    // ====================================

    /**
     * 批量更新请求
     */
    public static class BatchUpdateRequest {
        private List<Long> ids;
        private Boolean isActive;

        // Getters and Setters
        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }
}