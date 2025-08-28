package com.HuangYe.WildNest.service;

import com.HuangYe.WildNest.entity.Comment;
import com.HuangYe.WildNest.entity.Reply;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 留言服务接口
 */
public interface CommentService extends IService<Comment> {
    
    /**
     * 分页查询留言列表
     * @param page 页码
     * @param size 每页大小
     * @param category 分类
     * @return 留言分页列表
     */
    IPage<Comment> getCommentPage(Integer page, Integer size, String category);
    
    /**
     * 提交留言
     * @param comment 留言对象
     * @param userIp 用户IP
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean submitComment(Comment comment, String userIp, String deviceId);
    
    /**
     * 点赞留言
     * @param commentId 留言ID
     * @param userIp 用户IP
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean likeComment(Long commentId, String userIp, String deviceId);
    
    /**
     * 回复留言
     * @param reply 回复对象
     * @param userIp 用户IP
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean replyComment(Reply reply, String userIp, String deviceId);
    
    /**
     * 获取留言的回复列表
     * @param commentId 留言ID
     * @return 回复列表
     */
    List<Reply> getCommentReplies(Long commentId);
    
    /**
     * 删除留言
     * @param commentId 留言ID
     * @param userIp 用户IP
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean deleteComment(Long commentId, String userIp, String deviceId);
    
    /**
     * 删除回复
     * @param replyId 回复ID
     * @param userIp 用户IP
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean deleteReply(Long replyId, String userIp, String deviceId);
    
    /**
     * 获取热门留言
     * @param limit 限制数量
     * @return 热门留言列表
     */
    List<Comment> getHotComments(Integer limit);
    
    /**
     * 获取今日留言数量
     * @return 今日留言数量
     */
    Integer getTodayCommentCount();
    
    /**
     * 置顶/取消置顶留言（管理员功能）
     * @param commentId 留言ID
     * @param isPinned 是否置顶
     * @return 是否成功
     */
    boolean pinComment(Long commentId, Boolean isPinned);
    
    /**
     * 隐藏/显示留言（管理员功能）
     * @param commentId 留言ID
     * @param isHidden 是否隐藏
     * @return 是否成功
     */
    boolean hideComment(Long commentId, Boolean isHidden);
    
    /**
     * 检查用户是否已点赞
     * @param commentId 留言ID
     * @param userIp 用户IP
     * @param deviceId 设备ID
     * @return 是否已点赞
     */
    boolean hasUserLiked(Long commentId, String userIp, String deviceId);
    
    /**
     * 敏感词过滤
     * @param content 内容
     * @return 过滤后的内容
     */
    String filterSensitiveWords(String content);
}