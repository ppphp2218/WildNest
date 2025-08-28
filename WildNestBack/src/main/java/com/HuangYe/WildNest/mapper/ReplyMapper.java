package com.HuangYe.WildNest.mapper;

import com.HuangYe.WildNest.entity.Reply;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 回复Mapper接口
 */
@Mapper
public interface ReplyMapper extends BaseMapper<Reply> {
    
    /**
     * 根据留言ID查询回复列表
     * @param commentId 留言ID
     * @return 回复列表
     */
    @Select("SELECT * FROM reply WHERE comment_id = #{commentId} AND deleted = 0 AND status = 0 ORDER BY created_at ASC")
    List<Reply> selectByCommentId(@Param("commentId") Long commentId);
    
    /**
     * 根据留言ID统计回复数量
     * @param commentId 留言ID
     * @return 回复数量
     */
    @Select("SELECT COUNT(*) FROM reply WHERE comment_id = #{commentId} AND deleted = 0 AND status = 0")
    Integer countByCommentId(@Param("commentId") Long commentId);
    
    /**
     * 根据父回复ID查询子回复列表
     * @param parentId 父回复ID
     * @return 子回复列表
     */
    @Select("SELECT * FROM reply WHERE parent_id = #{parentId} AND deleted = 0 AND status = 0 ORDER BY created_at ASC")
    List<Reply> selectByParentId(@Param("parentId") Long parentId);
    
    /**
     * 更新回复状态
     * @param replyId 回复ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE reply SET status = #{status} WHERE id = #{replyId} AND deleted = 0")
    int updateReplyStatus(@Param("replyId") Long replyId, @Param("status") Integer status);
    
    /**
     * 批量删除留言的所有回复
     * @param commentId 留言ID
     * @return 影响行数
     */
    @Update("UPDATE reply SET deleted = 1 WHERE comment_id = #{commentId}")
    int deleteByCommentId(@Param("commentId") Long commentId);
}