package com.HuangYe.WildNest.service.impl;

import com.HuangYe.WildNest.entity.AdminUser;
import com.HuangYe.WildNest.mapper.AdminUserMapper;
import com.HuangYe.WildNest.service.AdminUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 管理员用户服务实现类
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    @Override
    public AdminUser login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return null;
        }
        
        try {
            // 查询管理员
            LambdaQueryWrapper<AdminUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AdminUser::getUsername, username)
                    .eq(AdminUser::getStatus, 1);
            
            AdminUser admin = getOne(queryWrapper);
            if (admin == null) {
                log.warn("管理员不存在: {}", username);
                return null;
            }
            
            // 验证密码
            if (!verifyPassword(password, admin.getPassword(), admin.getSalt())) {
                log.warn("管理员密码错误: {}", username);
                return null;
            }
            
            return admin;
            
        } catch (Exception e) {
            log.error("管理员登录异常: {}", username, e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLoginInfo(Long adminId, String loginIp) {
        if (adminId == null) {
            return false;
        }
        
        try {
            LambdaUpdateWrapper<AdminUser> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(AdminUser::getId, adminId)
                    .set(AdminUser::getLastLoginAt, LocalDateTime.now())
                    .set(AdminUser::getLastLoginIp, loginIp)
                    .setSql("login_count = login_count + 1");
            
            boolean result = update(updateWrapper);
            log.info("更新管理员登录信息: adminId={}, ip={}, result={}", adminId, loginIp, result);
            return result;
            
        } catch (Exception e) {
            log.error("更新管理员登录信息失败: adminId={}", adminId, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long adminId, String oldPassword, String newPassword) {
        if (adminId == null || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            return false;
        }
        
        try {
            // 查询管理员
            AdminUser admin = getById(adminId);
            if (admin == null) {
                log.warn("管理员不存在: {}", adminId);
                return false;
            }
            
            // 验证旧密码
            if (!verifyPassword(oldPassword, admin.getPassword(), admin.getSalt())) {
                log.warn("旧密码错误: {}", adminId);
                return false;
            }
            
            // 生成新盐值和密码
            String newSalt = generateSalt();
            String encodedPassword = encodePassword(newPassword, newSalt);
            
            // 更新密码
            LambdaUpdateWrapper<AdminUser> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(AdminUser::getId, adminId)
                    .set(AdminUser::getPassword, encodedPassword)
                    .set(AdminUser::getSalt, newSalt);
            
            boolean result = update(updateWrapper);
            log.info("管理员密码修改: adminId={}, result={}", adminId, result);
            return result;
            
        } catch (Exception e) {
            log.error("管理员密码修改失败: adminId={}", adminId, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createAdmin(AdminUser adminUser) {
        if (adminUser == null || !StringUtils.hasText(adminUser.getUsername()) || 
            !StringUtils.hasText(adminUser.getPassword())) {
            return false;
        }
        
        try {
            // 检查用户名是否已存在
            LambdaQueryWrapper<AdminUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AdminUser::getUsername, adminUser.getUsername());
            
            if (count(queryWrapper) > 0) {
                log.warn("管理员用户名已存在: {}", adminUser.getUsername());
                return false;
            }
            
            // 生成盐值和加密密码
            String salt = generateSalt();
            String encodedPassword = encodePassword(adminUser.getPassword(), salt);
            
            adminUser.setSalt(salt);
            adminUser.setPassword(encodedPassword);
            adminUser.setStatus(1);
            adminUser.setLoginCount(0);
            
            boolean result = save(adminUser);
            log.info("创建管理员: username={}, result={}", adminUser.getUsername(), result);
            return result;
            
        } catch (Exception e) {
            log.error("创建管理员失败: username={}", adminUser.getUsername(), e);
            throw e;
        }
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword, String salt) {
        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(encodedPassword) || 
            !StringUtils.hasText(salt)) {
            return false;
        }
        
        String encoded = encodePassword(rawPassword, salt);
        return encoded.equals(encodedPassword);
    }

    @Override
    public String encodePassword(String rawPassword, String salt) {
        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(salt)) {
            return null;
        }
        
        // 使用MD5加盐加密（实际项目中建议使用BCrypt等更安全的算法）
        String saltedPassword = rawPassword + salt;
        return DigestUtils.md5DigestAsHex(saltedPassword.getBytes());
    }
    
    /**
     * 生成随机盐值
     */
    private String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}