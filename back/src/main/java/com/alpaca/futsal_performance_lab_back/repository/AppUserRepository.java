package com.alpaca.futsal_performance_lab_back.repository;

import com.alpaca.futsal_performance_lab_back.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByName(String name);
    Optional<AppUser> findByUserId(String userId);
    Optional<AppUser> findByNameAndPhoneNumberAndUserId(String name, String userId, String phoneNumber);

}
