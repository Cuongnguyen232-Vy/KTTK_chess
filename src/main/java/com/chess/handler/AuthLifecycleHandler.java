package com.chess.handler;

import com.chess.model.Account;
import com.chess.model.enums.AccountState;
import com.chess.persistence.AccountPersistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * AuthLifecycleHandler - Xử lý các sự kiện đăng nhập / đăng xuất.
 * - Cập nhật trạng thái người chơi khi login/logout
 * - Điều hướng đến đúng trang sau khi đăng nhập (Admin → dashboard, User →
 * home)
 */
@Component
public class AuthLifecycleHandler implements AuthenticationSuccessHandler, LogoutHandler, LogoutSuccessHandler {

    @Autowired
    private AccountPersistence accountPersistence;

    /**
     * handleLoginSuccess - Gọi sau khi đăng nhập thành công.
     * - Cập nhật trạng thái ONLINE cho tài khoản
     * - Redirect theo vai trò: ADMIN → /dashboard, USER → /home
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();

        // Cập nhật trạng thái ONLINE
        Optional<Account> optAccount = accountPersistence.searchByUserName(username);
        optAccount.ifPresent(account -> {
            account.setState(AccountState.ONLINE);
            accountPersistence.commitAccountChange(account);
        });

        // Redirect theo role
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        if (role.equals("ROLE_ADMIN")) {
            response.sendRedirect("/dashboard");
        } else {
            response.sendRedirect("/home");
        }
    }

    /**
     * logout - Gọi TRƯỚC KHI session bị xoá.
     * Cập nhật trạng thái OFFLINE ở đây vì authentication chưa bị clear.
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            Optional<Account> optAccount = accountPersistence.searchByUserName(username);
            optAccount.ifPresent(account -> {
                account.setState(AccountState.OFFLINE);
                accountPersistence.commitAccountChange(account);
            });
        }
    }

    /**
     * onLogoutSuccess - Gọi sau khi đăng xuất.
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        response.sendRedirect("/login?logout=true");
    }
}
