package com.alpaca.futsal_performance_lab_back.service.user;

import org.springframework.web.multipart.MultipartFile;


public interface FileUploadService {
    String upload(MultipartFile file);
}
