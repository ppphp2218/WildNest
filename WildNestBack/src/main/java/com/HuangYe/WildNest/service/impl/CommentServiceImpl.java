package com.HuangYe.WildNest.service.impl;

import com.HuangYe.WildNest.entity.Comment;
import com.HuangYe.WildNest.entity.Reply;
import com.HuangYe.WildNest.mapper.CommentMapper;
import com.HuangYe.WildNest.mapper.ReplyMapper;
import com.HuangYe.WildNest.service.CommentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

// import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
// import java.util.concurrent.TimeUnit;

/**
 * 留言服务实现类
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private ReplyMapper replyMapper;
    
    // @Autowired
    // private RedisTemplate<String, Object> redisTemplate;
    
    // 简单的内存缓存，用于记录点赞状态（生产环境应使用Redis）
    private final java.util.Set<String> likeCache = java.util.concurrent.ConcurrentHashMap.newKeySet();
    
    // 敏感词列表（简单实现，实际项目中应该从配置文件或数据库读取）
    private static final List<String> SENSITIVE_WORDS = Arrays.asList(
        "政治", "色情", "广告", "垃圾", "spam"
    );
    
    // Redis key前缀
    private static final String LIKE_KEY_PREFIX = "comment:like:";
    private static final String COMMENT_CACHE_PREFIX = "comment:cache:";
    
    @Override
    public IPage<Comment> getCommentPage(Integer page, Integer size, String category) {
        Page<Comment> pageObj = new Page<>(page, size);
        return commentMapper.selectCommentPage(pageObj, category);
    }
    
    @Override
    @Transactional
    public boolean submitComment(Comment comment, String userIp, String deviceId) {
        try {
            // 敏感词过滤
            String filteredContent = filterSensitiveWords(comment.getContent());
            comment.setContent(filteredContent);
            
            // 设置基础信息
            comment.setUserIp(userIp);
            comment.setDeviceId(deviceId);
            comment.setLikeCount(0);
            comment.setReplyCount(0);
            comment.setIsPinned(false);
            comment.setStatus(0);
            // 时间字段由MyBatis-Plus自动填充
            
            // 检查是否包含敏感词
            if (containsSensitiveWords(comment.getContent())) {
                comment.setIsSensitive(true);
                comment.setStatus(1); // 隐藏状态，需要审核
            } else {
                comment.setIsSensitive(false);
            }
            
            return save(comment);
        } catch (Exception e) {
            log.error("提交留言失败", e);
            return false;
        }
    }
    
    @Override
    public boolean likeComment(Long commentId, String userIp, String deviceId) {
        try {
            // 检查是否已点赞
            if (hasUserLiked(commentId, userIp, deviceId)) {
                return false;
            }
            
            // 增加点赞数
            int result = commentMapper.increaseLikeCount(commentId);
            if (result > 0) {
                // 记录点赞状态
                String likeKey = LIKE_KEY_PREFIX + commentId + ":" + userIp + ":" + deviceId;
                likeCache.add(likeKey);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("点赞留言失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean replyComment(Reply reply, String userIp, String deviceId) {
        try {
            // 敏感词过滤
            String filteredContent = filterSensitiveWords(reply.getContent());
            reply.setContent(filteredContent);
            
            // 设置基础信息
            reply.setUserIp(userIp);
            reply.setDeviceId(deviceId);
            reply.setStatus(true); // true表示正常，false表示删除
            // 时间字段由MyBatis-Plus自动填充
            
            // 检查是否包含敏感词
            if (containsSensitiveWords(reply.getContent())) {
                reply.setStatus(false); // 隐藏状态，需要审核
            }
            
            // 保存回复
            boolean saveResult = replyMapper.insert(reply) > 0;
            
            if (saveResult) {
                // 增加留言的回复数
                commentMapper.increaseReplyCount(reply.getCommentId());
            }
            
            return saveResult;
        } catch (Exception e) {
            log.error("回复留言失败", e);
            return false;
        }
    }
    
    @Override
    public List<Reply> getCommentReplies(Long commentId) {
        return replyMapper.selectByCommentId(commentId);
    }
    
    @Override
    @Transactional
    public boolean deleteComment(Long commentId, String userIp, String deviceId) {
        try {
            // 验证是否为留言作者（简单实现，实际项目中需要更严格的验证）
            Comment comment = getById(commentId);
            if (comment == null) {
                return false;
            }
            
            // 检查是否为同一用户（通过IP和设备ID）
            if (!comment.getUserIp().equals(userIp) || !comment.getDeviceId().equals(deviceId)) {
                return false;
            }
            
            // 删除留言（逻辑删除）
            boolean deleteResult = removeById(commentId);
            
            if (deleteResult) {
                // 删除相关回复
                replyMapper.deleteByCommentId(commentId);
            }
            
            return deleteResult;
        } catch (Exception e) {
            log.error("删除留言失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean deleteReply(Long replyId, String userIp, String deviceId) {
        try {
            // 验证是否为回复作者
            Reply reply = replyMapper.selectById(replyId);
            if (reply == null) {
                return false;
            }
            
            // 检查是否为同一用户
            if (!reply.getUserIp().equals(userIp) || !reply.getDeviceId().equals(deviceId)) {
                return false;
            }
            
            // 删除回复（逻辑删除）
            boolean deleteResult = replyMapper.deleteById(replyId) > 0;
            
            if (deleteResult) {
                // 减少留言的回复数
                commentMapper.decreaseReplyCount(reply.getCommentId());
            }
            
            return deleteResult;
        } catch (Exception e) {
            log.error("删除回复失败", e);
            return false;
        }
    }
    
    @Override
    public List<Comment> getHotComments(Integer limit) {
        return commentMapper.selectHotComments(limit);
    }
    
    @Override
    public Integer getTodayCommentCount() {
        return commentMapper.getTodayCommentCount();
    }
    
    @Override
    public boolean pinComment(Long commentId, Boolean isPinned) {
        try {
            int pinnedValue = isPinned ? 1 : 0;
            return commentMapper.updatePinnedStatus(commentId, pinnedValue) > 0;
        } catch (Exception e) {
            log.error("置顶留言失败", e);
            return false;
        }
    }
    
    @Override
    public boolean hideComment(Long commentId, Boolean isHidden) {
        try {
            int status = isHidden ? 1 : 0;
            return commentMapper.updateCommentStatus(commentId, status) > 0;
        } catch (Exception e) {
            log.error("隐藏留言失败", e);
            return false;
        }
    }
    
    @Override
    public boolean hasUserLiked(Long commentId, String userIp, String deviceId) {
        String likeKey = LIKE_KEY_PREFIX + commentId + ":" + userIp + ":" + deviceId;
        return likeCache.contains(likeKey);
    }
    
    @Override
    public String filterSensitiveWords(String content) {
        if (!StringUtils.hasText(content)) {
            return content;
        }
        
        String filteredContent = content;
        for (String word : SENSITIVE_WORDS) {
            if (filteredContent.contains(word)) {
                filteredContent = filteredContent.replace(word, "***");
            }
        }
        
        return filteredContent;
    }
    
    /**
     * 检查内容是否包含敏感词
     */
    private boolean containsSensitiveWords(String content) {
        if (!StringUtils.hasText(content)) {
            return false;
        }
        
        for (String word : SENSITIVE_WORDS) {
            if (content.contains(word)) {
                return true;
            }
        }
        
        return false;
    }
}