package com.chess.repository;

import com.chess.model.Invitation;
import com.chess.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByTargetUserNameAndStatus(String targetUserName, RequestStatus status);
    Optional<Invitation> findById(Long id);
}
