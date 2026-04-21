package com.chess.controller;

import com.chess.payload.MovePayload;
import com.chess.service.PlaySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * ChessGameController - Xử lý các nước đi trong ván cờ qua WebSocket.
 * Client gửi nước đi → /app/move/{sessionId}
 * Server broadcast → /topic/game/{sessionId}
 */
@Controller
public class ChessGameController {

    @Autowired
    private PlaySessionService playSessionService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * handleMove - Nhận nước đi từ một người chơi, lưu DB và broadcast cho đối thủ.
     * @param sessionId ID ván cờ
     * @param move      MovePayload
     * @param principal Người chơi hiện tại
     */
    @MessageMapping("/move/{sessionId}")
    public void handleMove(@DestinationVariable Long sessionId,
                           MovePayload move,
                           Principal principal) {
        // Lưu nước đi vào DB
        if (move.getPlayerId() != null) {
            playSessionService.saveMove(
                    sessionId,
                    move.getFromCell(),
                    move.getToCell(),
                    move.getBoardFen(),
                    move.getPlayerId()
            );
        }

        // Kết thúc ván: xử lý kết quả và ELO
        if (move.isGameOver()) {
            playSessionService.endSession(sessionId, move.getWinner());
        }

        // Broadcast nước đi cho cả 2 người
        simpMessagingTemplate.convertAndSend("/topic/game/" + sessionId, move);
    }

    /**
     * handleResign - Người chơi đầu hàng.
     */
    @MessageMapping("/resign/{sessionId}")
    public void handleResign(@DestinationVariable Long sessionId,
                             MovePayload payload,
                             Principal principal) {
        // Người đầu hàng thua, người kia thắng
        String winner = payload.getWinner(); // Đã được set ở client
        playSessionService.endSession(sessionId, winner);

        payload.setGameOver(true);
        simpMessagingTemplate.convertAndSend("/topic/game/" + sessionId, payload);
    }

    @MessageMapping("/draw-offer/{sessionId}")
    public void handleDrawOffer(@DestinationVariable Long sessionId, MovePayload payload) {
        simpMessagingTemplate.convertAndSend("/topic/game/" + sessionId + "/draw-offer", payload);
    }

    @MessageMapping("/draw-reject/{sessionId}")
    public void handleDrawReject(@DestinationVariable Long sessionId, MovePayload payload) {
        simpMessagingTemplate.convertAndSend("/topic/game/" + sessionId + "/draw-reject", payload);
    }
}
