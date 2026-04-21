package com.chess.controller;

import com.chess.model.Account;
import com.chess.service.AccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

/**
 * RankingController - Hiển thị bảng xếp hạng ELO.
 * Module V trong báo cáo.
 */
@Controller
public class RankingController {

    @Autowired
    private AccountManagerService accountManagerService;

    @Autowired
    private com.chess.repository.AccountRepository accountRepository;

    @Autowired
    private com.chess.repository.SessionMemberRepository sessionMemberRepository;

    /**
     * showRanking - Lấy danh sách người chơi sắp xếp theo ELO giảm dần.
     */
    @GetMapping("/ranking")
    public String showRanking(Model model) {
        List<Account> players = accountManagerService.getAllSortedByElo();
        model.addAttribute("players", players);
        return "ranking_view";
    }

    /**
     * showProfile - Lịch sử trận đấu của một người chơi.
     */
    @GetMapping("/profile/{username}")
    public String showProfile(@org.springframework.web.bind.annotation.PathVariable("username") String username, Model model) {
        Account targetPlayer = accountRepository.findByUserName(username).orElse(null);
        if (targetPlayer == null) return "redirect:/ranking";
        
        List<com.chess.model.SessionMember> matchHistory = sessionMemberRepository.findByUserUserNameOrderByIdDesc(username);
        
        model.addAttribute("targetPlayer", targetPlayer);
        model.addAttribute("matchHistory", matchHistory);
        return "profile_view";
    }
}
