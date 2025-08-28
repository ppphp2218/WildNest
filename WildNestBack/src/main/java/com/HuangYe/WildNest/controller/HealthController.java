package com.HuangYe.WildNest.controller;

import com.HuangYe.WildNest.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@Slf4j
@RestController
@RequestMapping("/health")
@Tag(name = "健康检查", description = "系统健康检查相关接口")
public class HealthController extends BaseController {
    
    /**
     * 健康检查接口
     */
    @GetMapping("/check")
    @Operation(summary = "健康检查", description = "检查系统运行状态")
    public Result<Map<String, Object>> healthCheck() {
        log.info("执行健康检查");
        
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("application", "WildNest");
        healthInfo.put("version", "1.0.0");
        healthInfo.put("description", "WildNest 酒吧H5项目后端服务");
        
        return Result.success("系统运行正常", healthInfo);
    }
    
    /**
     * 版本信息接口
     */
    @GetMapping("/version")
    @Operation(summary = "版本信息", description = "获取系统版本信息")
    public Result<Map<String, Object>> version() {
        Map<String, Object> versionInfo = new HashMap<>();
        versionInfo.put("application", "WildNest");
        versionInfo.put("version", "1.0.0");
        versionInfo.put("buildTime", "2024-01-20");
        versionInfo.put("author", "HuangYe");
        versionInfo.put("description", "WildNest 酒吧H5项目 - 为您提供最佳的酒品推荐体验");
        
        return Result.success(versionInfo);
    }
}