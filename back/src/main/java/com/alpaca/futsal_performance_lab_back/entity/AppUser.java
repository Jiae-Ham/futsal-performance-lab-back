package com.alpaca.futsal_performance_lab_back.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AppUser {
    @Id
    @Column(name = "user_id", nullable = false, unique = true, length = 20)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String name; // 사용자 실명

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(name = "phone_number",nullable = false, length = 11)
    private String phoneNumber;

    @Column(name = "main_position", length = 20)
    private String mainPosition; // 주 포지션

    @Column(name = "dominant_foot", length = 20) // VARCHAR(10)
    private String dominantFoot; // 주 발

    @Column(precision = 4, scale = 1)
    private BigDecimal weight; // 몸무게

    @Column(precision = 4, scale = 1)
    private BigDecimal height; // 키

    @Column(name = "birth_date")
    private LocalDate birthDate; // 생년월일
    
    @Column(name= "profile_image_url", length = 512)
    private String profileImageUrl;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<GameAssign> gameAssigns = new ArrayList<>();

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Summary> summaries = new ArrayList<>();
}
