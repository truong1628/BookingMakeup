package com.booking.bookingmakeup.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

    public static String saveFile(String uploadDir, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        // Tạo thư mục nếu chưa tồn tại
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file ngẫu nhiên bằng UUID để tránh bị trùng tên file ảnh
        String originalFileName = multipartFile.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // Lưu file vào thư mục
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFileName; // Trả về tên file mới để lưu vào DB
        } catch (IOException ioe) {
            throw new IOException("Không thể lưu file ảnh: " + originalFileName, ioe);
        }
    }
}