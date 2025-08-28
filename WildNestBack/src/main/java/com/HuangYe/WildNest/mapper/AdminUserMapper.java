package com.HuangYe.WildNest.mapper;

import com.HuangYe.WildNest.entity.AdminUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 管理员用户Mapper接口
 * 
 * @author HuangYe
 * @since 2024
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {

    /**
     * 根据用户名查询管理员
     * 
     * @param username 用户名
     * @return 管理员信息
     */
    @Select("SELECT * FROM admin_user WHERE username = #{username} AND status = 1")
    AdminUser selectByUsername(String username);

    /**
     * 统计管理员数量
     * 
     * @return 管理员数量
     */
    @Select("SELECT COUNT(*) FROM admin_user WHERE status = 1")
    Integer countActiveAdmins();
}