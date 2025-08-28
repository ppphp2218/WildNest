package com.HuangYe.WildNest.controller;

import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/**
 * 基础控制器类
 * 提供通用的控制器功能
 */
@RestController
public abstract class BaseController {
    
    /**
     * 获取客户端真实IP地址
     * 
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    protected String getClientIpAddress(HttpServletRequest request) {
        // 获取X-Forwarded-For头信息
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // X-Forwarded-For可能包含多个IP，取第一个
            int index = xForwardedFor.indexOf(",");
            if (index != -1) {
                return xForwardedFor.substring(0, index).trim();
            } else {
                return xForwardedFor.trim();
            }
        }
        
        // 获取X-Real-IP头信息
        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp) && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp.trim();
        }
        
        // 获取Proxy-Client-IP头信息
        String proxyClientIp = request.getHeader("Proxy-Client-IP");
        if (StringUtils.hasText(proxyClientIp) && !"unknown".equalsIgnoreCase(proxyClientIp)) {
            return proxyClientIp.trim();
        }
        
        // 获取WL-Proxy-Client-IP头信息
        String wlProxyClientIp = request.getHeader("WL-Proxy-Client-IP");
        if (StringUtils.hasText(wlProxyClientIp) && !"unknown".equalsIgnoreCase(wlProxyClientIp)) {
            return wlProxyClientIp.trim();
        }
        
        // 获取HTTP_CLIENT_IP头信息
        String httpClientIp = request.getHeader("HTTP_CLIENT_IP");
        if (StringUtils.hasText(httpClientIp) && !"unknown".equalsIgnoreCase(httpClientIp)) {
            return httpClientIp.trim();
        }
        
        // 获取HTTP_X_FORWARDED_FOR头信息
        String httpXForwardedFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (StringUtils.hasText(httpXForwardedFor) && !"unknown".equalsIgnoreCase(httpXForwardedFor)) {
            return httpXForwardedFor.trim();
        }
        
        // 如果以上都没有获取到，则使用request.getRemoteAddr()
        String remoteAddr = request.getRemoteAddr();
        
        // 处理IPv6本地地址
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr) || "::1".equals(remoteAddr)) {
            return "127.0.0.1";
        }
        
        return StringUtils.hasText(remoteAddr) ? remoteAddr.trim() : "unknown";
    }
    
    /**
     * 获取用户代理信息
     * 
     * @param request HTTP请求对象
     * @return 用户代理字符串
     */
    protected String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return StringUtils.hasText(userAgent) ? userAgent : "unknown";
    }
    
    /**
     * 判断是否为移动端请求
     * 
     * @param request HTTP请求对象
     * @return 是否为移动端
     */
    protected boolean isMobileRequest(HttpServletRequest request) {
        String userAgent = getUserAgent(request).toLowerCase();
        return userAgent.contains("mobile") || 
               userAgent.contains("android") || 
               userAgent.contains("iphone") || 
               userAgent.contains("ipad") || 
               userAgent.contains("windows phone");
    }
}