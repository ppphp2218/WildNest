package com.HuangYe.WildNest.service.impl;

import com.HuangYe.WildNest.common.Constants;
import com.HuangYe.WildNest.config.OssConfig;
import com.HuangYe.WildNest.service.OssService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * OSS服务实现类
 */
@Slf4j
@Service
public class OssServiceImpl implements OssService {
    
    private final OSS ossClient;
    private final OssConfig ossConfig;
    
    public OssServiceImpl(OSS ossClient, OssConfig ossConfig) {
        this.ossClient = ossClient;
        this.ossConfig = ossConfig;
    }
    
    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedImageType(fileExtension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + fileExtension);
        }
        
        // 验证文件大小
        if (file.getSize() > Constants.Upload.MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小超过限制: " + Constants.Upload.MAX_FILE_SIZE + " bytes");
        }
        
        try {
            // 生成文件名
            String fileName = generateFileName(originalFilename);
            String objectKey = folder + "/" + fileName;
            
            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossConfig.getBucketName(), 
                objectKey, 
                file.getInputStream()
            );
            
            ossClient.putObject(putObjectRequest);
            
            // 返回文件访问URL
            return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + objectKey;
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return false;
        }
        
        try {
            // 从URL中提取对象键
            String objectKey = extractObjectKeyFromUrl(fileUrl);
            if (!StringUtils.hasText(objectKey)) {
                return false;
            }
            
            ossClient.deleteObject(ossConfig.getBucketName(), objectKey);
            return true;
            
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileUrl, e);
            return false;
        }
    }
    
    @Override
    public String generatePresignedUrl(String objectKey, long expiration) {
        try {
            Date expirationDate = new Date(System.currentTimeMillis() + expiration * 1000);
            URL url = ossClient.generatePresignedUrl(ossConfig.getBucketName(), objectKey, expirationDate);
            return url.toString();
        } catch (Exception e) {
            log.error("生成预签名URL失败: {}", objectKey, e);
            return null;
        }
    }
    
    @Override
    public boolean doesObjectExist(String objectKey) {
        try {
            return ossClient.doesObjectExist(ossConfig.getBucketName(), objectKey);
        } catch (Exception e) {
            log.error("检查文件是否存在失败: {}", objectKey, e);
            return false;
        }
    }
    
    /**
     * 生成唯一文件名
     */
    private String generateFileName(String originalFilename) {
        String fileExtension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return timestamp + "_" + uuid + "." + fileExtension;
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
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * 检查是否为允许的图片类型
     */
    private boolean isAllowedImageType(String fileExtension) {
        return Arrays.asList(Constants.Upload.ALLOWED_IMAGE_TYPES).contains(fileExtension);
    }
    
    /**
     * 从URL中提取对象键
     */
    private String extractObjectKeyFromUrl(String fileUrl) {
        try {
            String bucketDomain = ossConfig.getBucketName() + "." + ossConfig.getEndpoint();
            if (fileUrl.contains(bucketDomain)) {
                int index = fileUrl.indexOf(bucketDomain) + bucketDomain.length() + 1;
                return fileUrl.substring(index);
            }
        } catch (Exception e) {
            log.error("提取对象键失败: {}", fileUrl, e);
        }
        return null;
    }
}