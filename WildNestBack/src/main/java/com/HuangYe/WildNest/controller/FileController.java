package com.HuangYe.WildNest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件访问控制器
 */
@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {
    
    @Value("${file.upload.path:/tmp/uploads}")
    private String uploadPath;
    
    /**
     * 访问上传的图片文件
     */
    @GetMapping("/images/**")
    public ResponseEntity<Resource> getImage(@RequestParam String path) {
        try {
            // 构建文件路径
            String filePath = uploadPath + "/images/" + path;
            File file = new File(filePath);
            
            // 检查文件是否存在
            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }
            
            // 检查文件是否在允许的目录内（防止路径遍历攻击）
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path requestedFile = Paths.get(filePath).toAbsolutePath().normalize();
            
            if (!requestedFile.startsWith(uploadDir)) {
                log.warn("尝试访问不安全的文件路径: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            // 创建资源
            Resource resource = new FileSystemResource(file);
            
            // 确定内容类型
            String contentType = Files.probeContentType(requestedFile);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            // 返回文件
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("访问文件失败: {}", path, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 获取文件信息
     */
    @GetMapping("/info")
    public ResponseEntity<Object> getFileInfo(@RequestParam String path) {
        try {
            // 构建文件路径
            String filePath = uploadPath + "/" + path;
            File file = new File(filePath);
            
            // 检查文件是否存在
            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }
            
            // 检查文件是否在允许的目录内
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path requestedFile = Paths.get(filePath).toAbsolutePath().normalize();
            
            if (!requestedFile.startsWith(uploadDir)) {
                log.warn("尝试访问不安全的文件路径: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            // 构建文件信息
            java.util.Map<String, Object> fileInfo = new java.util.HashMap<>();
            fileInfo.put("name", file.getName());
            fileInfo.put("size", file.length());
            fileInfo.put("lastModified", file.lastModified());
            fileInfo.put("path", path);
            
            try {
                String contentType = Files.probeContentType(requestedFile);
                fileInfo.put("contentType", contentType);
            } catch (Exception e) {
                fileInfo.put("contentType", "unknown");
            }
            
            return ResponseEntity.ok(fileInfo);
            
        } catch (Exception e) {
            log.error("获取文件信息失败: {}", path, e);
            return ResponseEntity.notFound().build();
        }
    }
}