package com.alpaca.futsal_performance_lab_back.dto;

import com.alpaca.futsal_performance_lab_back.entity.AppUser;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserSignUpRequestDTO {
    private String userId;
    private String password;
    private String name;
    private String nickname;
    private String phoneNumber;
    private String mainPosition;
    private String dominantFoot;
    private BigDecimal weight;
    private BigDecimal height;
    private LocalDate birthDate;
    private String profileImageUrl;
    private LocalDateTime createdAt;

    public static AppUser fromDTO(AppUserSignUpRequestDTO dto) {
        return AppUser.builder()
                .userId(dto.getUserId())
                .password(dto.getPassword())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .phoneNumber(dto.getPhoneNumber())
                .mainPosition(dto.getMainPosition())
                .dominantFoot(dto.getDominantFoot())
                .weight(dto.getWeight())
                .height(dto.getHeight())
                .birthDate(dto.getBirthDate())
                .profileImageUrl(dto.getProfileImageUrl())
                .createdAt(dto.getCreatedAt())
                .build();
    }
    public static AppUserSignUpRequestDTO fromEntity(AppUser user) {
        return AppUserSignUpRequestDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .mainPosition(user.getMainPosition())
                .dominantFoot(user.getDominantFoot())
                .weight(user.getWeight())
                .height(user.getHeight())
                .birthDate(user.getBirthDate())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }

}
