package com.HuangYe.WildNest.controller;

import com.HuangYe.WildNest.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传", description = "文件上传相关接口")
public class UploadController {
    
    @Value("${file.upload.path:/tmp/uploads}")
    private String uploadPath;
    
    @Value("${file.upload.url-prefix:http://localhost:8080/api/files}")
    private String urlPrefix;
    
    // 允许的图片格式
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    // 最大文件大小（5MB）
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    /**
     * 上传图片
     */
    @PostMapping("/image")
    @Operation(summary = "上传图片")
    public Result<Map<String, String>> uploadImage(
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file) {
        
        try {
            // 验证文件
            String validationResult = validateImageFile(file);
            if (validationResult != null) {
                return Result.error(validationResult);
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String fileName = generateFileName() + extension;
            
            // 创建目录
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String dirPath = uploadPath + "/images/" + datePath;
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 保存文件
            String filePath = dirPath + "/" + fileName;
            File destFile = new File(filePath);
            file.transferTo(destFile);
            
            // 生成访问URL
            String fileUrl = urlPrefix + "/images/" + datePath + "/" + fileName;
            
            // 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", fileName);
            result.put("originalName", originalFilename);
            result.put("size", String.valueOf(file.getSize()));
            
            log.info("图片上传成功: {}", fileUrl);
            return Result.success(result);
            
        } catch (IOException e) {
            log.error("图片上传失败", e);
            return Result.error("图片上传失败");
        } catch (Exception e) {
            log.error("图片上传异常", e);
            return Result.error("图片上传异常");
        }
    }
    
    /**
     * 批量上传图片
     */
    @PostMapping("/images")
    @Operation(summary = "批量上传图片")
    public Result<List<Map<String, String>>> uploadImages(
            @Parameter(description = "图片文件列表") @RequestParam("files") MultipartFile[] files) {
        
        try {
            if (files == null || files.length == 0) {
                return Result.error("请选择要上传的文件");
            }
            
            if (files.length > 5) {
                return Result.error("最多只能上传5张图片");
            }
            
            List<Map<String, String>> results = new ArrayList<>();
            
            for (MultipartFile file : files) {
                // 验证文件
                String validationResult = validateImageFile(file);
                if (validationResult != null) {
                    return Result.error(validationResult);
                }
                
                // 生成文件名
                String originalFilename = file.getOriginalFilename();
                String extension = getFileExtension(originalFilename);
                String fileName = generateFileName() + extension;
                
                // 创建目录
                String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                String dirPath = uploadPath + "/images/" + datePath;
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                
                // 保存文件
                String filePath = dirPath + "/" + fileName;
                File destFile = new File(filePath);
                file.transferTo(destFile);
                
                // 生成访问URL
                String fileUrl = urlPrefix + "/images/" + datePath + "/" + fileName;
                
                // 添加到结果列表
                Map<String, String> result = new HashMap<>();
                result.put("url", fileUrl);
                result.put("filename", fileName);
                result.put("originalName", originalFilename);
                result.put("size", String.valueOf(file.getSize()));
                
                results.add(result);
            }
            
            log.info("批量图片上传成功，共上传{}张图片", results.size());
            return Result.success(results);
            
        } catch (IOException e) {
            log.error("批量图片上传失败", e);
            return Result.error("批量图片上传失败");
        } catch (Exception e) {
            log.error("批量图片上传异常", e);
            return Result.error("批量图片上传异常");
        }
    }
    
    /**
     * 验证图片文件
     */
    private String validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "请选择要上传的文件";
        }
        
        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            return "文件大小不能超过5MB";
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            return "只支持JPG、PNG、GIF、WebP格式的图片";
        }
        
        // 检查文件名
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            return "文件名不能为空";
        }
        
        return null; // 验证通过
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        
        return filename.substring(lastDotIndex).toLowerCase();
    }
    
    /**
     * 生成唯一文件名
     */
    private String generateFileName() {
        return System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}