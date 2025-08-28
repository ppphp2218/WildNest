package com.HuangYe.WildNest.controller;

import com.HuangYe.WildNest.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库查询控制器
 */
@Slf4j
@RestController
@RequestMapping("/database")
@Tag(name = "数据库查询接口", description = "用于查询数据库表结构和数据")
public class DatabaseController {
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * 获取所有表信息
     */
    @GetMapping("/tables")
    @Operation(summary = "获取所有表信息")
    public Result<List<Map<String, Object>>> getAllTables() {
        try {
            List<Map<String, Object>> tables = new ArrayList<>();
            
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                
                // 获取所有表
                try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (rs.next()) {
                        Map<String, Object> table = new HashMap<>();
                        table.put("tableName", rs.getString("TABLE_NAME"));
                        table.put("tableType", rs.getString("TABLE_TYPE"));
                        table.put("remarks", rs.getString("REMARKS"));
                        tables.add(table);
                    }
                }
            }
            
            return Result.success(tables);
        } catch (Exception e) {
            log.error("获取表信息失败", e);
            return Result.error("获取表信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定表的结构信息
     */
    @GetMapping("/table/{tableName}/structure")
    @Operation(summary = "获取表结构信息")
    public Result<Map<String, Object>> getTableStructure(@PathVariable String tableName) {
        try {
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> columns = new ArrayList<>();
            List<Map<String, Object>> indexes = new ArrayList<>();
            
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                
                // 获取列信息
                try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {
                    while (rs.next()) {
                        Map<String, Object> column = new HashMap<>();
                        column.put("columnName", rs.getString("COLUMN_NAME"));
                        column.put("dataType", rs.getString("TYPE_NAME"));
                        column.put("columnSize", rs.getInt("COLUMN_SIZE"));
                        column.put("nullable", rs.getInt("NULLABLE") == 1);
                        column.put("defaultValue", rs.getString("COLUMN_DEF"));
                        column.put("remarks", rs.getString("REMARKS"));
                        column.put("autoIncrement", "YES".equals(rs.getString("IS_AUTOINCREMENT")));
                        columns.add(column);
                    }
                }
                
                // 获取主键信息
                List<String> primaryKeys = new ArrayList<>();
                try (ResultSet rs = metaData.getPrimaryKeys(null, null, tableName)) {
                    while (rs.next()) {
                        primaryKeys.add(rs.getString("COLUMN_NAME"));
                    }
                }
                
                // 获取索引信息
                try (ResultSet rs = metaData.getIndexInfo(null, null, tableName, false, false)) {
                    while (rs.next()) {
                        Map<String, Object> index = new HashMap<>();
                        index.put("indexName", rs.getString("INDEX_NAME"));
                        index.put("columnName", rs.getString("COLUMN_NAME"));
                        index.put("unique", !rs.getBoolean("NON_UNIQUE"));
                        indexes.add(index);
                    }
                }
                
                result.put("tableName", tableName);
                result.put("columns", columns);
                result.put("primaryKeys", primaryKeys);
                result.put("indexes", indexes);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取表结构失败: {}", tableName, e);
            return Result.error("获取表结构失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取指定表的数据
     */
    @GetMapping("/table/{tableName}/data")
    @Operation(summary = "获取表数据")
    public Result<Map<String, Object>> getTableData(@PathVariable String tableName) {
        try {
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> rows = new ArrayList<>();
            
            try (Connection connection = dataSource.getConnection()) {
                // 限制查询前20条数据
                String sql = "SELECT * FROM " + tableName + " LIMIT 20";
                
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    
                    // 获取列信息
                    var metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    List<String> columnNames = new ArrayList<>();
                    
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames.add(metaData.getColumnName(i));
                    }
                    
                    // 获取数据行
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (String columnName : columnNames) {
                            row.put(columnName, rs.getObject(columnName));
                        }
                        rows.add(row);
                    }
                }
                
                // 获取总行数
                String countSql = "SELECT COUNT(*) as total FROM " + tableName;
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(countSql)) {
                    if (rs.next()) {
                        result.put("totalCount", rs.getInt("total"));
                    }
                }
            }
            
            result.put("tableName", tableName);
            result.put("rows", rows);
            result.put("displayCount", rows.size());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取表数据失败: {}", tableName, e);
            return Result.error("获取表数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取数据库基本信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取数据库基本信息")
    public Result<Map<String, Object>> getDatabaseInfo() {
        try {
            Map<String, Object> info = new HashMap<>();
            
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                
                info.put("databaseProductName", metaData.getDatabaseProductName());
                info.put("databaseProductVersion", metaData.getDatabaseProductVersion());
                info.put("driverName", metaData.getDriverName());
                info.put("driverVersion", metaData.getDriverVersion());
                info.put("url", metaData.getURL());
                info.put("userName", metaData.getUserName());
                
                // 获取当前数据库名
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT DATABASE() as db_name")) {
                    if (rs.next()) {
                        info.put("currentDatabase", rs.getString("db_name"));
                    }
                }
            }
            
            return Result.success(info);
        } catch (Exception e) {
            log.error("获取数据库信息失败", e);
            return Result.error("获取数据库信息失败: " + e.getMessage());
        }
    }
}