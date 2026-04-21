package com.chess.repository;

import com.chess.model.StepDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepDetailRepository extends JpaRepository<StepDetail, Long> {
    List<StepDetail> findBySessionIdOrderByIdAsc(Long sessionId);
}
