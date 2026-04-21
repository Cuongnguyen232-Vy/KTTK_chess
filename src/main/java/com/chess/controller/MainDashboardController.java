package com.chess.controller;

import com.chess.persistence.AccountPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MainDashboardController - Hiển thị trang chủ sau khi đăng nhập.
 * Admin → /dashboard (quản lý hệ thống)
 * User → /home (giao diện người chơi)
 */
@Controller
public class MainDashboardController {

    @Autowired
    private AccountPersistence accountPersistence;

    /**
     * handleDashboard - Trang chủ dành cho ADMIN.
     */
    @GetMapping("/dashboard")
    public String handleDashboard(Authentication auth, Model model) {
        String username = auth.getName();
        accountPersistence.searchByUserName(username).ifPresent(acc ->
                model.addAttribute("account", acc)
        );
        return "dashboard";
    }

    /**
     * home - Trang chủ dành cho USER (người chơi).
     */
    @GetMapping("/home")
    public String home(Authentication auth, Model model) {
        String username = auth.getName();
        accountPersistence.searchByUserName(username).ifPresent(acc ->
                model.addAttribute("account", acc)
        );
        return "home";
    }
}
