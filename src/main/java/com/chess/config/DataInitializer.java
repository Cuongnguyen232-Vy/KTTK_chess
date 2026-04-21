package com.chess.config;

import com.chess.model.Account;
import com.chess.model.enums.AccountState;
import com.chess.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DataInitializer - Tạo tài khoản mặc định khi khởi động lần đầu.
 * Admin: username=admin / password=admin123
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Reset trạng thái tất cả người dùng về OFFLINE khi khởi động lại server
        List<Account> allAccounts = accountRepository.findAll();
        for (Account acc : allAccounts) {
            acc.setState(AccountState.OFFLINE);
        }
        accountRepository.saveAll(allAccounts);

        // Tạo admin nếu chưa có
        if (!accountRepository.existsByUserName("admin")) {
            Account admin = new Account();
            admin.setUserName("admin");
            admin.setPassWord(passwordEncoder.encode("admin123"));
            admin.setFullName("Nguyễn Mạnh Cường");
            admin.setEloPoint(2000);
            admin.setUserRole("ADMIN");
            admin.setState(AccountState.OFFLINE);
            accountRepository.save(admin);
            System.out.println("✔ Đã tạo tài khoản admin: admin / admin123");
        }

        createSampleUser("nguyenvana", "Nguyễn Văn A", 1513);
        createSampleUser("tranthib",   "Trần Thị B",   1487);
        createSampleUser("levanc",     "Lê Văn C",     1650);
    }

    private void createSampleUser(String username, String fullName, int elo) {
        if (!accountRepository.existsByUserName(username)) {
            Account user = new Account();
            user.setUserName(username);
            user.setPassWord(passwordEncoder.encode("123456"));
            user.setFullName(fullName);
            user.setEloPoint(elo);
            user.setUserRole("USER");
            user.setState(AccountState.OFFLINE);
            accountRepository.save(user);
        }
    }
}
