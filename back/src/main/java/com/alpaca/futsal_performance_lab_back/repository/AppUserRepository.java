package com.alpaca.futsal_performance_lab_back.repository;

import com.alpaca.futsal_performance_lab_back.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
}
