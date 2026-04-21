package com.chess.controller;

import com.chess.model.Account;
import com.chess.model.enums.AccountState;
import com.chess.persistence.AccountPersistence;
import com.chess.service.AccountService;
import com.chess.service.PlaySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * GameWebSocketController - Điều phối giao diện game.
 * accountService: AccountService
 * showOnlinePlayers() : Lấy danh sách người chơi online
 */
@Controller
public class GameWebSocketController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountPersistence accountPersistence;

    @Autowired
    private PlaySessionService playSessionService;

    /**
     * showOnlinePlayers - Hiển thị danh sách người đang online.
     */
    @GetMapping("/online-players")
    public String showOnlinePlayers(Authentication auth, Model model) {
        List<Account> onlinePlayers = accountService.getOnlineAccounts();
        String currentUser = auth.getName();

        // Loại bỏ chính mình và các tài khoản ADMIN khỏi danh sách
        onlinePlayers.removeIf(p -> p.getUserName().equals(currentUser) || "ADMIN".equals(p.getUserRole()));

        model.addAttribute("players", onlinePlayers);
        accountPersistence.searchByUserName(currentUser)
                .ifPresent(acc -> model.addAttribute("currentAccount", acc));
        return "online_players";
    }

    /**
     * playGame - Hiển thị bàn cờ.
     */
    @GetMapping("/play-game")
    public String playGame(@RequestParam Long sessionId,
                           Authentication auth,
                           Model model) {
        String username = auth.getName();

        // Lấy thông tin người chơi hiện tại trong session
        playSessionService.findMemberBySessionAndUser(sessionId, username).ifPresent(member -> {
            model.addAttribute("myColor", member.getRole().name());
            model.addAttribute("myElo", member.getEloInitial());
        });

        // Lấy toàn bộ thành viên trong session để hiển thị đối thủ
        var members = playSessionService.findBySessionId(sessionId);
        members.stream()
                .filter(m -> !m.getUser().getUserName().equals(username))
                .findFirst()
                .ifPresent(opponent -> {
                    model.addAttribute("opponent", opponent.getUser());
                    model.addAttribute("opponentColor", opponent.getRole().name());
                });

        accountPersistence.searchByUserName(username)
                .ifPresent(acc -> model.addAttribute("currentAccount", acc));

        model.addAttribute("sessionId", sessionId);
        model.addAttribute("currentUsername", username);
        return "play_game";
    }
}
