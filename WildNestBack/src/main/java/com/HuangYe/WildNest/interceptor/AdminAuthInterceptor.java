package com.HuangYe.WildNest.interceptor;

import com.HuangYe.WildNest.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理员认证拦截器
 * 简单的Session认证机制
 * 
 * @author HuangYe
 * @since 2024
 */
@Slf4j
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final String ADMIN_SESSION_KEY = "admin_user";
    private static final String ADMIN_TOKEN_HEADER = "Admin-Token";
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查Session认证
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(ADMIN_SESSION_KEY) != null) {
            return true;
        }
        
        // 检查简单Token认证（可选）
        String token = request.getHeader(ADMIN_TOKEN_HEADER);
        if (StringUtils.hasText(token) && isValidAdminToken(token)) {
            return true;
        }
        
        // 认证失败，返回401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Result<String> result = Result.error("管理员未登录，请先登录");
        response.getWriter().write(objectMapper.writeValueAsString(result));
        
        log.warn("管理员未认证访问: {}", request.getRequestURI());
        return false;
    }
    
    /**
     * 验证管理员Token（简单实现）
     * 实际项目中可以使用更复杂的Token机制
     */
    private boolean isValidAdminToken(String token) {
        // 这里可以实现简单的Token验证逻辑
        // 例如：检查Token格式、过期时间等
        return "admin-token-2024".equals(token);
    }
}