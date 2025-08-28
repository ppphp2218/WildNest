package com.HuangYe.WildNest.controller;

import com.HuangYe.WildNest.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/test")
@Tag(name = "测试接口", description = "用于测试数据库连接和表结构")
public class TestController {
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * 测试数据库连接
     */
    @GetMapping("/db-connection")
    @Operation(summary = "测试数据库连接")
    public Result<Map<String, Object>> testDbConnection() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                
                result.put("connected", true);
                result.put("databaseProductName", metaData.getDatabaseProductName());
                result.put("databaseProductVersion", metaData.getDatabaseProductVersion());
                result.put("url", metaData.getURL());
                result.put("userName", metaData.getUserName());
                
                // 检查表是否存在
                List<String> tables = new ArrayList<>();
                try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (rs.next()) {
                        String tableName = rs.getString("TABLE_NAME");
                        if (tableName.toLowerCase().contains("comment") || 
                            tableName.toLowerCase().contains("drink") ||
                            tableName.toLowerCase().contains("category")) {
                            tables.add(tableName);
                        }
                    }
                }
                result.put("relevantTables", tables);
                
                return Result.success(result);
            }
        } catch (Exception e) {
            log.error("数据库连接测试失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("connected", false);
            errorResult.put("error", e.getMessage());
            errorResult.put("errorType", e.getClass().getSimpleName());
            Result<Map<String, Object>> errorResponse = Result.error("数据库连接失败: " + e.getMessage());
            errorResponse.setData(errorResult);
            return errorResponse;
        }
    }
    
    /**
     * 测试简单查询
     */
    @GetMapping("/simple-query")
    @Operation(summary = "测试简单查询")
    public Result<Map<String, Object>> testSimpleQuery() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            try (Connection connection = dataSource.getConnection()) {
                // 测试简单的查询
                try (var stmt = connection.createStatement()) {
                    try (var rs = stmt.executeQuery("SELECT 1 as test_value")) {
                        if (rs.next()) {
                            result.put("queryResult", rs.getInt("test_value"));
                            result.put("querySuccess", true);
                        }
                    }
                }
                
                return Result.success(result);
            }
        } catch (Exception e) {
            log.error("简单查询测试失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("querySuccess", false);
            errorResult.put("error", e.getMessage());
            Result<Map<String, Object>> errorResponse = Result.error("查询失败: " + e.getMessage());
            errorResponse.setData(errorResult);
            return errorResponse;
        }
    }
}