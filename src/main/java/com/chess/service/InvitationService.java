package com.chess.service;

import com.chess.model.Account;
import com.chess.model.Invitation;
import com.chess.model.enums.RequestStatus;
import com.chess.repository.AccountRepository;
import com.chess.repository.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepo;

    @Autowired
    private AccountRepository accountRepo;

    public Invitation createInvitation(Long initiatorId, Long targetId) {
        Account initiator = accountRepo.findById(initiatorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user: " + initiatorId));
        Account target = accountRepo.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user: " + targetId));

        Invitation inv = new Invitation();
        inv.setInitiator(initiator);
        inv.setTarget(target);
        inv.setSentTime(LocalDateTime.now());
        inv.setStatus(RequestStatus.PENDING);
        return invitationRepo.save(inv);
    }

    public Invitation updateStatus(Long invitationId, RequestStatus status) {
        Invitation inv = invitationRepo.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy invitation: " + invitationId));
        inv.setStatus(status);
        return invitationRepo.save(inv);
    }

    public Optional<Invitation> findById(Long id) {
        return invitationRepo.findById(id);
    }
}
