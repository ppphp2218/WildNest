package com.HuangYe.WildNest.mapper;

import com.HuangYe.WildNest.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 留言Mapper接口
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
    /**
     * 分页查询留言列表
     * @param page 分页对象
     * @param category 分类
     * @return 留言分页列表
     */
    @Select("<script>" +
            "SELECT * FROM comment " +
            "WHERE deleted = 0 AND status = 0 " +
            "<if test='category != null and category != \"all\"'>" +
            "AND category = #{category} " +
            "</if>" +
            "ORDER BY is_pinned DESC, created_at DESC" +
            "</script>")
    IPage<Comment> selectCommentPage(Page<Comment> page, @Param("category") String category);
    
    /**
     * 增加点赞数
     * @param commentId 留言ID
     * @return 影响行数
     */
    @Update("UPDATE comment SET like_count = like_count + 1 WHERE id = #{commentId} AND deleted = 0")
    int increaseLikeCount(@Param("commentId") Long commentId);
    
    /**
     * 增加回复数
     * @param commentId 留言ID
     * @return 影响行数
     */
    @Update("UPDATE comment SET reply_count = reply_count + 1 WHERE id = #{commentId} AND deleted = 0")
    int increaseReplyCount(@Param("commentId") Long commentId);
    
    /**
     * 减少回复数
     * @param commentId 留言ID
     * @return 影响行数
     */
    @Update("UPDATE comment SET reply_count = reply_count - 1 WHERE id = #{commentId} AND deleted = 0 AND reply_count > 0")
    int decreaseReplyCount(@Param("commentId") Long commentId);
    
    /**
     * 获取热门留言（按点赞数排序）
     * @param limit 限制数量
     * @return 热门留言列表
     */
    @Select("SELECT * FROM comment WHERE deleted = 0 AND status = 0 ORDER BY like_count DESC, created_at DESC LIMIT #{limit}")
    List<Comment> selectHotComments(@Param("limit") Integer limit);
    
    /**
     * 获取今日留言数量
     * @return 今日留言数量
     */
    @Select("SELECT COUNT(*) FROM comment WHERE deleted = 0 AND DATE(created_at) = CURDATE()")
    Integer getTodayCommentCount();
    
    /**
     * 置顶/取消置顶留言
     * @param commentId 留言ID
     * @param isPinned 是否置顶
     * @return 影响行数
     */
    @Update("UPDATE comment SET is_pinned = #{isPinned} WHERE id = #{commentId} AND deleted = 0")
    int updatePinnedStatus(@Param("commentId") Long commentId, @Param("isPinned") Integer isPinned);
    
    /**
     * 更新留言状态
     * @param commentId 留言ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE comment SET status = #{status} WHERE id = #{commentId} AND deleted = 0")
    int updateCommentStatus(@Param("commentId") Long commentId, @Param("status") Integer status);
}