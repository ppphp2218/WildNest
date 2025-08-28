package com.HuangYe.WildNest.controller.admin;

import com.HuangYe.WildNest.common.Result;
import com.HuangYe.WildNest.controller.BaseController;
import com.HuangYe.WildNest.entity.AdminUser;
import com.HuangYe.WildNest.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员认证控制器
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "管理员认证", description = "管理员登录认证相关接口")
public class AdminAuthController extends BaseController {

    private final AdminUserService adminUserService;
    private static final String ADMIN_SESSION_KEY = "admin_user";

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "管理员账号密码登录")
    public Result<Map<String, Object>> login(
            @Parameter(description = "用户名", required = true)
            @RequestParam String username,
            @Parameter(description = "密码", required = true)
            @RequestParam String password,
            HttpServletRequest request) {
        
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Result.error("用户名和密码不能为空");
        }
        
        try {
            // 验证管理员账号密码
            AdminUser admin = adminUserService.login(username, password);
            if (admin == null) {
                return Result.error("用户名或密码错误");
            }
            
            // 创建Session
            HttpSession session = request.getSession(true);
            session.setAttribute(ADMIN_SESSION_KEY, admin.getId());
            session.setMaxInactiveInterval(24 * 60 * 60); // 24小时
            
            // 更新登录信息
            adminUserService.updateLoginInfo(admin.getId(), request.getRemoteAddr());
            
            // 返回登录信息
            Map<String, Object> result = new HashMap<>();
            result.put("adminId", admin.getId());
            result.put("username", admin.getUsername());
            result.put("realName", admin.getRealName());
            result.put("sessionId", session.getId());
            result.put("token", "admin-token-2024"); // 简单Token
            
            log.info("管理员登录成功: {}", username);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("管理员登录失败: {}", username, e);
            return Result.error("登录失败，请稍后重试");
        }
    }

    /**
     * 管理员登出
     */
    @PostMapping("/logout")
    @Operation(summary = "管理员登出", description = "清除登录状态")
    public Result<String> logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            log.info("管理员登出成功");
            return Result.success("登出成功");
            
        } catch (Exception e) {
            log.error("管理员登出失败", e);
            return Result.error("登出失败");
        }
    }

    /**
     * 获取当前登录管理员信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取管理员信息", description = "获取当前登录的管理员信息")
    public Result<AdminUser> getAdminInfo(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                return Result.error("未登录");
            }
            
            Long adminId = (Long) session.getAttribute(ADMIN_SESSION_KEY);
            if (adminId == null) {
                return Result.error("未登录");
            }
            
            AdminUser admin = adminUserService.getById(adminId);
            if (admin == null) {
                return Result.error("管理员不存在");
            }
            
            // 清除敏感信息
            admin.setPassword(null);
            admin.setSalt(null);
            
            return Result.success(admin);
            
        } catch (Exception e) {
            log.error("获取管理员信息失败", e);
            return Result.error("获取信息失败");
        }
    }
}