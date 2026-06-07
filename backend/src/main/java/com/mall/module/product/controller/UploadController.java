package com.mall.module.product.controller;

import com.mall.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {

    private static final String UPLOAD_DIR = "uploads";
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png"};
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static Path baseUploadPath;

    @PostConstruct
    public void init() {
        baseUploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
        try {
            if (!Files.exists(baseUploadPath)) {
                Files.createDirectories(baseUploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录: " + baseUploadPath, e);
        }
    }

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.error("文件名不能为空");
        }

        String extension = getFileExtension(originalFilename);
        if (!isAllowedExtension(extension)) {
            return Result.error("只允许上传 JPG/JPEG/PNG 格式的图片");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.error("图片大小不能超过 2MB");
        }

        try {
            String savePath = saveFile(file, extension);
            return Result.success(savePath);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }

    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    private String saveFile(MultipartFile file, String extension) throws IOException {
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path datePath = baseUploadPath.resolve(dateDir);
        
        if (!Files.exists(datePath)) {
            Files.createDirectories(datePath);
        }

        String newFilename = UUID.randomUUID().toString() + "." + extension;
        Path filePath = datePath.resolve(newFilename);
        
        File dest = filePath.toFile();
        file.transferTo(dest);
        
        return "/api/uploads/" + dateDir + "/" + newFilename;
    }
}
