package com.alpaca.futsal_performance_lab_back.dto.auth;

import com.alpaca.futsal_performance_lab_back.entity.AppUser;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDTO {
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
    private LocalDateTime updatedAt;

    public static AppUserDTO fromEntity(AppUser user) {
        return AppUserDTO.builder()
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
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static AppUser fromDTO(AppUserDTO dto) {
        return AppUser.builder()
                .userId(dto.getUserId())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .phoneNumber(dto.getPhoneNumber())
                .mainPosition(dto.getMainPosition())
                .dominantFoot(dto.getDominantFoot())
                .weight(dto.getWeight())
                .height(dto.getHeight())
                .birthDate(dto.getBirthDate())
                .profileImageUrl(dto.getProfileImageUrl())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}
