package com.HuangYe.WildNest.controller.admin;

import com.HuangYe.WildNest.common.Result;
import com.HuangYe.WildNest.entity.Comment;
import com.HuangYe.WildNest.service.CommentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员留言板管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/comments")
@Tag(name = "管理员留言板管理", description = "管理员留言板管理相关接口")
public class AdminCommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 分页查询所有留言（包括隐藏的）
     */
    @GetMapping
    @Operation(summary = "分页查询所有留言")
    public Result<IPage<Comment>> getAllComments(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "分类", example = "all") @RequestParam(defaultValue = "all") String category,
            @Parameter(description = "状态", example = "all") @RequestParam(defaultValue = "all") String status) {
        
        try {
            // 管理员可以查看所有状态的留言
            IPage<Comment> commentPage = commentService.getCommentPage(page, size, category);
            return Result.success(commentPage);
        } catch (Exception e) {
            log.error("查询留言列表失败", e);
            return Result.error("查询留言列表失败");
        }
    }
    
    /**
     * 置顶/取消置顶留言
     */
    @PutMapping("/{commentId}/pin")
    @Operation(summary = "置顶/取消置顶留言")
    public Result<String> pinComment(
            @Parameter(description = "留言ID") @PathVariable Long commentId,
            @Parameter(description = "是否置顶") @RequestParam Boolean isPinned) {
        
        try {
            boolean success = commentService.pinComment(commentId, isPinned);
            if (success) {
                String action = isPinned ? "置顶" : "取消置顶";
                return Result.success(action + "成功");
            } else {
                return Result.error("操作失败");
            }
        } catch (Exception e) {
            log.error("置顶留言失败", e);
            return Result.error("操作失败");
        }
    }
    
    /**
     * 隐藏/显示留言
     */
    @PutMapping("/{commentId}/hide")
    @Operation(summary = "隐藏/显示留言")
    public Result<String> hideComment(
            @Parameter(description = "留言ID") @PathVariable Long commentId,
            @Parameter(description = "是否隐藏") @RequestParam Boolean isHidden) {
        
        try {
            boolean success = commentService.hideComment(commentId, isHidden);
            if (success) {
                String action = isHidden ? "隐藏" : "显示";
                return Result.success(action + "成功");
            } else {
                return Result.error("操作失败");
            }
        } catch (Exception e) {
            log.error("隐藏留言失败", e);
            return Result.error("操作失败");
        }
    }
    
    /**
     * 删除留言（管理员权限）
     */
    @DeleteMapping("/{commentId}")
    @Operation(summary = "删除留言")
    public Result<String> deleteComment(
            @Parameter(description = "留言ID") @PathVariable Long commentId) {
        
        try {
            boolean success = commentService.removeById(commentId);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除留言失败", e);
            return Result.error("删除失败");
        }
    }
    
    /**
     * 批量删除留言
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除留言")
    public Result<String> batchDeleteComments(
            @Parameter(description = "留言ID列表") @RequestBody List<Long> commentIds) {
        
        try {
            boolean success = commentService.removeByIds(commentIds);
            if (success) {
                return Result.success("批量删除成功");
            } else {
                return Result.error("批量删除失败");
            }
        } catch (Exception e) {
            log.error("批量删除留言失败", e);
            return Result.error("批量删除失败");
        }
    }
    
    /**
     * 批量置顶留言
     */
    @PutMapping("/batch/pin")
    @Operation(summary = "批量置顶留言")
    public Result<String> batchPinComments(
            @Parameter(description = "留言ID列表") @RequestBody List<Long> commentIds,
            @Parameter(description = "是否置顶") @RequestParam Boolean isPinned) {
        
        try {
            int successCount = 0;
            for (Long commentId : commentIds) {
                if (commentService.pinComment(commentId, isPinned)) {
                    successCount++;
                }
            }
            
            String action = isPinned ? "置顶" : "取消置顶";
            return Result.success(String.format("批量%s成功，共处理%d条记录", action, successCount));
        } catch (Exception e) {
            log.error("批量置顶留言失败", e);
            return Result.error("批量操作失败");
        }
    }
    
    /**
     * 获取留言统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取留言统计信息")
    public Result<Object> getCommentStats() {
        try {
            // 今日留言数
            Integer todayCount = commentService.getTodayCommentCount();
            
            // 热门留言
            List<Comment> hotComments = commentService.getHotComments(10);
            
            // 总留言数
            long totalCount = commentService.count();
            
            // 构建统计信息
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("todayCount", todayCount);
            stats.put("totalCount", totalCount);
            stats.put("hotComments", hotComments);
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取留言统计信息失败", e);
            return Result.error("获取统计信息失败");
        }
    }
    
    /**
     * 获取留言详情
     */
    @GetMapping("/{commentId}")
    @Operation(summary = "获取留言详情")
    public Result<Comment> getCommentDetail(
            @Parameter(description = "留言ID") @PathVariable Long commentId) {
        
        try {
            Comment comment = commentService.getById(commentId);
            if (comment != null) {
                return Result.success(comment);
            } else {
                return Result.error("留言不存在");
            }
        } catch (Exception e) {
            log.error("获取留言详情失败", e);
            return Result.error("获取留言详情失败");
        }
    }
}