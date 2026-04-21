package com.chess.controller;

import com.chess.model.Invitation;
import com.chess.model.PlaySession;
import com.chess.model.enums.RequestStatus;
import com.chess.payload.*;
import com.chess.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * MatchmakingController - Xử lý ghép cặp người chơi qua WebSocket.
 * invitationService: InvitationService
 * playSessionService: PlaySessionService
 * simpMessagingTemplate: SimpMessagingTemplate
 *
 * Luồng:
 *  1. Player A gửi lời mời → /app/invite
 *  2. Player B nhận qua /user/{username}/queue/invite
 *  3. Player B phản hồi → /app/respond
 *  4. Nếu chấp nhận: tạo PlaySession → thông báo cả 2 chơi → /user/{username}/queue/game-start
 */
@Controller
public class MatchmakingController {

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private PlaySessionService playSessionService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * sendInvitation - Gửi lời mời thách đấu.
     * @param payload InvitationPayload
     * @param principal Người dùng hiện tại (Spring Security)
     */
    @MessageMapping("/invite")
    public void sendInvitation(InvitationPayload payload, Principal principal) {
        // Tạo và lưu lời mời vào DB
        Invitation inv = invitationService.createInvitation(
                payload.getInitiatorId(),
                payload.getTargetId()
        );

        // Cập nhật ID lời mời để target có thể phản hồi
        payload.setInvitationId(inv.getId());

        // Gửi thông báo đến player B
        simpMessagingTemplate.convertAndSendToUser(
                payload.getTargetUsername(),
                "/queue/invite",
                payload
        );
    }

    /**
     * respondToInvitation - Phản hồi lời mời thách đấu (chấp nhận / từ chối).
     * @param payload ResponsePayload
     * @param principal Người dùng hiện tại
     */
    @MessageMapping("/respond")
    public void respondToInvitation(ResponsePayload payload, Principal principal) {
        if (payload.isAccepted()) {
            // Chấp nhận → tạo PlaySession
            invitationService.updateStatus(payload.getInvitationId(), RequestStatus.ACCEPTED);

            PlaySession session = playSessionService.initializeSession(
                    payload.getInitiatorId(),
                    payload.getTargetId()
            );

            // Thông báo cho người MỜI (WHITE)
            GameStartPayload forInitiator = new GameStartPayload(
                    session.getId(),
                    payload.getInitiatorUsername(),
                    payload.getTargetUsername(),
                    "WHITE"
            );
            simpMessagingTemplate.convertAndSendToUser(
                    payload.getInitiatorUsername(),
                    "/queue/game-start",
                    forInitiator
            );

            // Thông báo cho người ĐƯỢC MỜI (BLACK)
            GameStartPayload forTarget = new GameStartPayload(
                    session.getId(),
                    payload.getInitiatorUsername(),
                    payload.getTargetUsername(),
                    "BLACK"
            );
            simpMessagingTemplate.convertAndSendToUser(
                    payload.getTargetUsername(),
                    "/queue/game-start",
                    forTarget
            );

        } else {
            // Từ chối → cập nhật trạng thái
            invitationService.updateStatus(payload.getInvitationId(), RequestStatus.REJECTED);

            // Thông báo cho người mời biết bị từ chối
            simpMessagingTemplate.convertAndSendToUser(
                    payload.getInitiatorUsername(),
                    "/queue/invite-rejected",
                    payload
            );
        }
    }
}
