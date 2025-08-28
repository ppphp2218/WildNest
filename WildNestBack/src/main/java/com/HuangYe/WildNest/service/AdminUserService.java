package com.HuangYe.WildNest.service;

import com.HuangYe.WildNest.entity.AdminUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 管理员用户服务接口
 * 
 * @author HuangYe
 * @since 2024
 */
public interface AdminUserService extends IService<AdminUser> {

    /**
     * 管理员登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return 管理员信息，登录失败返回null
     */
    AdminUser login(String username, String password);

    /**
     * 更新登录信息
     * 
     * @param adminId 管理员ID
     * @param loginIp 登录IP
     * @return 更新结果
     */
    boolean updateLoginInfo(Long adminId, String loginIp);

    /**
     * 修改密码
     * 
     * @param adminId 管理员ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    boolean changePassword(Long adminId, String oldPassword, String newPassword);

    /**
     * 创建管理员
     * 
     * @param adminUser 管理员信息
     * @return 创建结果
     */
    boolean createAdmin(AdminUser adminUser);

    /**
     * 验证密码
     * 
     * @param rawPassword 原始密码
     * @param encodedPassword 加密密码
     * @param salt 盐值
     * @return 验证结果
     */
    boolean verifyPassword(String rawPassword, String encodedPassword, String salt);

    /**
     * 加密密码
     * 
     * @param rawPassword 原始密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    String encodePassword(String rawPassword, String salt);
}