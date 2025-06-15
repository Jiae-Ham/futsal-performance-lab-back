package com.alpaca.futsal_performance_lab_back.repository;


import com.alpaca.futsal_performance_lab_back.entity.SetAssign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetAssignRepository extends JpaRepository<SetAssign, Integer> {
    List<SetAssign> findByGame_GameId(Integer gameId);
}
