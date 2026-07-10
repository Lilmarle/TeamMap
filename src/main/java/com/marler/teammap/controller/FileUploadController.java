package com.marler.teammap.controller;

import com.marler.teammap.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.url}")
    private String uploadUrl;

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        log.info("头像上传请求 - 文件名: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());

        if (file.isEmpty()) {
            log.warn("头像上传失败：文件为空");
            return Result.error("上传文件不能为空");
        }

        // 校验文件类型
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String[] allowedSuffixes = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
        boolean valid = false;
        for (String s : allowedSuffixes) {
            if (s.equalsIgnoreCase(suffix)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            log.warn("头像上传失败：不支持的文件格式 - {}", suffix);
            return Result.error("仅支持 JPG/PNG/GIF/BMP/WEBP 格式的图片");
        }

        // 生成唯一文件名
        String newFilename = UUID.randomUUID().toString() + suffix;

        // 确保上传目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            file.transferTo(new File(uploadPath, newFilename));
        } catch (IOException e) {
            log.error("头像上传失败：文件写入异常 - {}", e.getMessage());
            return Result.error("文件上传失败，请稍后重试");
        }

        // 返回可访问的 URL
        String avatarUrl = uploadUrl + "/" + newFilename;
        log.info("头像上传成功 - URL: {}", avatarUrl);
        return Result.success(avatarUrl);
    }
}
