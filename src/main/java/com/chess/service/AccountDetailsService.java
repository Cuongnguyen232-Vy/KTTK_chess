package com.chess.service;

import com.chess.model.Account;
import com.chess.persistence.AccountPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * AccountDetailsService - Tích hợp với Spring Security.
 * Triển khai interface UserDetailsService để xác thực người dùng.
 */
@Service
public class AccountDetailsService implements UserDetailsService {

    @Autowired
    private AccountPersistence accountPersistence;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return fetchUserCredentials(username);
    }

    /**
     * Hàm truy xuất định danh từ hệ thống.
     * @param u Kiểu String - Tên đăng nhập của người dùng
     * @return UserDetails - Đối tượng chứa thông tin xác thực và quyền hạn
     */
    public UserDetails fetchUserCredentials(String u) {
        Account account = accountPersistence.searchByUserName(u)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + u));

        return new User(
                account.getUserName(),
                account.getPassWord(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + account.getUserRole())
                )
        );
    }
}
