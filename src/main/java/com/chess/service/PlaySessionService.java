package com.chess.service;

import com.chess.model.*;
import com.chess.model.enums.*;
import com.chess.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlaySessionService {

    @Autowired private PlaySessionRepository    playSessionRepo;
    @Autowired private SessionMemberRepository  sessionMemberRepo;
    @Autowired private StepDetailRepository     stepDetailRepo;
    @Autowired private AccountRepository        accountRepo;

    /** Khởi tạo phiên chơi khi lời mời được chấp nhận */
    public PlaySession initializeSession(Long initiatorId, Long targetId) {
        Account initiator = accountRepo.findById(initiatorId).orElseThrow();
        Account target    = accountRepo.findById(targetId).orElseThrow();

        PlaySession session = new PlaySession();
        session.setStartTime(LocalDateTime.now());
        session = playSessionRepo.save(session);

        // Người mời = TRẮNG
        SessionMember whiteMember = new SessionMember();
        whiteMember.setUser(initiator);
        whiteMember.setSession(session);
        whiteMember.setRole(GameRole.WHITE);
        whiteMember.setEloInitial(initiator.getEloPoint());
        sessionMemberRepo.save(whiteMember);

        // Người được mời = ĐEN
        SessionMember blackMember = new SessionMember();
        blackMember.setUser(target);
        blackMember.setSession(session);
        blackMember.setRole(GameRole.BLACK);
        blackMember.setEloInitial(target.getEloPoint());
        sessionMemberRepo.save(blackMember);

        // Cập nhật trạng thái IN_GAME
        initiator.setState(AccountState.IN_GAME);
        target.setState(AccountState.IN_GAME);
        accountRepo.save(initiator);
        accountRepo.save(target);

        return session;
    }

    /** Lưu nước đi */
    public void saveMove(Long sessionId, String fromCell, String toCell, String boardFen, Long playerId) {
        PlaySession session = playSessionRepo.findById(sessionId).orElseThrow();
        Account player = accountRepo.findById(playerId).orElseThrow();

        StepDetail step = new StepDetail();
        step.setFromCell(fromCell);
        step.setToCell(toCell);
        step.setBoardFen(boardFen);
        step.setPlayer(player);
        step.setSession(session);
        stepDetailRepo.save(step);
    }

    /** Kết thúc ván - cập nhật ELO */
    public void endSession(Long sessionId, String winnerUsername) {
        PlaySession session = playSessionRepo.findById(sessionId).orElseThrow();
        session.setEndTime(LocalDateTime.now());
        playSessionRepo.save(session);

        List<SessionMember> members = sessionMemberRepo.findBySessionId(sessionId);
        for (SessionMember member : members) {
            Account user = member.getUser();
            member.setEloFinal(user.getEloPoint());

            if (winnerUsername == null) {
                member.setResult(MatchEnding.DRAW);
                user.setEloPoint(user.getEloPoint() + 3);
            } else if (user.getUserName().equals(winnerUsername)) {
                member.setResult(MatchEnding.VICTORY);
                user.setEloPoint(user.getEloPoint() + 15);
            } else {
                member.setResult(MatchEnding.DEFEAT);
                user.setEloPoint(Math.max(0, user.getEloPoint() - 10));
            }
            user.setState(AccountState.ONLINE);
            accountRepo.save(user);
            sessionMemberRepo.save(member);
        }
    }

    public List<SessionMember> findBySessionId(Long sessionId) {
        return sessionMemberRepo.findBySessionId(sessionId);
    }

    public Optional<PlaySession> findById(Long id) {
        return playSessionRepo.findById(id);
    }

    public Optional<SessionMember> findMemberBySessionAndUser(Long sessionId, String username) {
        return sessionMemberRepo.findBySessionIdAndUserUserName(sessionId, username);
    }
}
