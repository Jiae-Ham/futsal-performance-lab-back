package com.alpaca.futsal_performance_lab_back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 윈도우 기준 절대 경로 → 정적 URL 매핑
        registry.addResourceHandler("/images/profile/**")
                .addResourceLocations("file:///C:/app/uploads/profile/");
    }
}
