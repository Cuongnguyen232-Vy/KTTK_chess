package com.chess.repository;

import com.chess.model.PlaySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaySessionRepository extends JpaRepository<PlaySession, Long> {
}
