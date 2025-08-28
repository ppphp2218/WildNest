package com.HuangYe.WildNest.controller;

import com.HuangYe.WildNest.common.Result;
import com.HuangYe.WildNest.entity.Comment;
import com.HuangYe.WildNest.entity.Reply;
import com.HuangYe.WildNest.service.CommentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 留言板控制器
 */
@Slf4j
@RestController
@RequestMapping("/comments")
@Tag(name = "留言板管理", description = "留言板相关接口")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 分页查询留言列表
     */
    @GetMapping
    @Operation(summary = "分页查询留言列表")
    public Result<IPage<Comment>> getComments(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "分类", example = "all") @RequestParam(defaultValue = "all") String category) {
        
        try {
            IPage<Comment> commentPage = commentService.getCommentPage(page, size, category);
            return Result.success(commentPage);
        } catch (Exception e) {
            log.error("查询留言列表失败", e);
            return Result.error("查询留言列表失败");
        }
    }
    
    /**
     * 提交留言
     */
    @PostMapping
    @Operation(summary = "提交留言")
    public Result<String> submitComment(@RequestBody Comment comment, HttpServletRequest request) {
        try {
            // 参数验证
            if (!StringUtils.hasText(comment.getUsername())) {
                return Result.error("昵称不能为空");
            }
            if (!StringUtils.hasText(comment.getContent())) {
                return Result.error("留言内容不能为空");
            }
            if (comment.getContent().length() > 500) {
                return Result.error("留言内容不能超过500字");
            }
            
            // 获取用户IP和设备信息
            String userIp = getClientIpAddress(request);
            String deviceId = request.getHeader("Device-ID");
            if (!StringUtils.hasText(deviceId)) {
                deviceId = userIp; // 如果没有设备ID，使用IP作为标识
            }
            
            // 设置默认分类
            if (!StringUtils.hasText(comment.getCategory())) {
                comment.setCategory("general");
            }
            
            boolean success = commentService.submitComment(comment, userIp, deviceId);
            if (success) {
                return Result.success("留言提交成功");
            } else {
                return Result.error("留言提交失败");
            }
        } catch (Exception e) {
            log.error("提交留言失败", e);
            return Result.error("提交留言失败");
        }
    }
    
    /**
     * 点赞留言
     */
    @PostMapping("/{commentId}/like")
    @Operation(summary = "点赞留言")
    public Result<String> likeComment(
            @Parameter(description = "留言ID") @PathVariable Long commentId,
            HttpServletRequest request) {
        
        try {
            String userIp = getClientIpAddress(request);
            String deviceId = request.getHeader("Device-ID");
            if (!StringUtils.hasText(deviceId)) {
                deviceId = userIp;
            }
            
            // 检查是否已点赞
            if (commentService.hasUserLiked(commentId, userIp, deviceId)) {
                return Result.error("您已经点过赞了");
            }
            
            boolean success = commentService.likeComment(commentId, userIp, deviceId);
            if (success) {
                return Result.success("点赞成功");
            } else {
                return Result.error("点赞失败");
            }
        } catch (Exception e) {
            log.error("点赞留言失败", e);
            return Result.error("点赞失败");
        }
    }
    
    /**
     * 回复留言
     */
    @PostMapping("/{commentId}/reply")
    @Operation(summary = "回复留言")
    public Result<String> replyComment(
            @Parameter(description = "留言ID") @PathVariable Long commentId,
            @RequestBody Reply reply,
            HttpServletRequest request) {
        
        try {
            // 参数验证
            if (!StringUtils.hasText(reply.getUsername())) {
                return Result.error("昵称不能为空");
            }
            if (!StringUtils.hasText(reply.getContent())) {
                return Result.error("回复内容不能为空");
            }
            if (reply.getContent().length() > 200) {
                return Result.error("回复内容不能超过200字");
            }
            
            // 设置留言ID
            reply.setCommentId(commentId);
            
            // 获取用户IP和设备信息
            String userIp = getClientIpAddress(request);
            String deviceId = request.getHeader("Device-ID");
            if (!StringUtils.hasText(deviceId)) {
                deviceId = userIp;
            }
            
            boolean success = commentService.replyComment(reply, userIp, deviceId);
            if (success) {
                return Result.success("回复成功");
            } else {
                return Result.error("回复失败");
            }
        } catch (Exception e) {
            log.error("回复留言失败", e);
            return Result.error("回复失败");
        }
    }
    
    /**
     * 获取留言的回复列表
     */
    @GetMapping("/{commentId}/replies")
    @Operation(summary = "获取留言的回复列表")
    public Result<List<Reply>> getCommentReplies(
            @Parameter(description = "留言ID") @PathVariable Long commentId) {
        
        try {
            List<Reply> replies = commentService.getCommentReplies(commentId);
            return Result.success(replies);
        } catch (Exception e) {
            log.error("查询回复列表失败", e);
            return Result.error("查询回复列表失败");
        }
    }
    
    /**
     * 删除留言
     */
    @DeleteMapping("/{commentId}")
    @Operation(summary = "删除留言")
    public Result<String> deleteComment(
            @Parameter(description = "留言ID") @PathVariable Long commentId,
            HttpServletRequest request) {
        
        try {
            String userIp = getClientIpAddress(request);
            String deviceId = request.getHeader("Device-ID");
            if (!StringUtils.hasText(deviceId)) {
                deviceId = userIp;
            }
            
            boolean success = commentService.deleteComment(commentId, userIp, deviceId);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败，只能删除自己的留言");
            }
        } catch (Exception e) {
            log.error("删除留言失败", e);
            return Result.error("删除失败");
        }
    }
    
    /**
     * 删除回复
     */
    @DeleteMapping("/replies/{replyId}")
    @Operation(summary = "删除回复")
    public Result<String> deleteReply(
            @Parameter(description = "回复ID") @PathVariable Long replyId,
            HttpServletRequest request) {
        
        try {
            String userIp = getClientIpAddress(request);
            String deviceId = request.getHeader("Device-ID");
            if (!StringUtils.hasText(deviceId)) {
                deviceId = userIp;
            }
            
            boolean success = commentService.deleteReply(replyId, userIp, deviceId);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败，只能删除自己的回复");
            }
        } catch (Exception e) {
            log.error("删除回复失败", e);
            return Result.error("删除失败");
        }
    }
    
    /**
     * 获取热门留言
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门留言")
    public Result<List<Comment>> getHotComments(
            @Parameter(description = "限制数量", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<Comment> hotComments = commentService.getHotComments(limit);
            return Result.success(hotComments);
        } catch (Exception e) {
            log.error("查询热门留言失败", e);
            return Result.error("查询热门留言失败");
        }
    }
    
    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp) && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}