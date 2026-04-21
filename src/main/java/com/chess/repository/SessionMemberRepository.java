package com.chess.repository;

import com.chess.model.SessionMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionMemberRepository extends JpaRepository<SessionMember, Long> {
    List<SessionMember> findBySessionId(Long sessionId);
    Optional<SessionMember> findBySessionIdAndUserUserName(Long sessionId, String userName);
}
