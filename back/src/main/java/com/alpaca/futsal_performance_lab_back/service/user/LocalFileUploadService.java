package com.alpaca.futsal_performance_lab_back.service.user;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileUploadService implements FileUploadService{

    private final String uploadDir = "C:/app/uploads/profile"; // 예: static/profile

    @Override
    public String upload(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String fullPath = uploadDir + File.separator + fileName;

            // 디렉토리 없으면 생성
            Files.createDirectories(Paths.get(uploadDir));
            file.transferTo(new File(fullPath));

            // 클라이언트가 접근 가능한 URL 리턴
            return "/images/profile/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}
