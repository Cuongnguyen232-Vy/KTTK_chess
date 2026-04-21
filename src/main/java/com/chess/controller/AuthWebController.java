package com.chess.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * AuthWebController - Xử lý hiển thị form đăng nhập.
 * Việc xác thực thực sự do Spring Security + SecurityConfig đảm nhiệm.
 */
@Controller
public class AuthWebController {

    @GetMapping("/login")
    public String showLoginForm() {
        return "login_view";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }
}
