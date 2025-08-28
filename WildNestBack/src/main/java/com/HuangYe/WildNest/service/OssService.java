package com.HuangYe.WildNest.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * OSS服务接口
 */
public interface OssService {
    
    /**
     * 上传文件
     * @param file 文件
     * @param folder 文件夹路径
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String folder);
    
    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);
    
    /**
     * 生成预签名URL
     * @param objectKey 对象键
     * @param expiration 过期时间（秒）
     * @return 预签名URL
     */
    String generatePresignedUrl(String objectKey, long expiration);
    
    /**
     * 检查文件是否存在
     * @param objectKey 对象键
     * @return 是否存在
     */
    boolean doesObjectExist(String objectKey);
}