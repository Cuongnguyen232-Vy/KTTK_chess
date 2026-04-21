package com.chess.persistence;

import com.chess.model.Account;
import com.chess.model.enums.AccountState;
import com.chess.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * AccountPersistence - Lớp trung gian giữa Service và Repository.
 * Đóng gói các thao tác dữ liệu với bảng account.
 */
@Component
public class AccountPersistence {

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Tra cứu thông tin tài khoản theo tên đăng nhập.
     * @param un Kiểu String - Tên đăng nhập cụ thể cần tìm
     * @return Optional<Account> - Đối tượng bao bọc dữ liệu Account
     */
    public Optional<Account> searchByUserName(String un) {
        return accountRepository.findByUserName(un);
    }

    /**
     * Truy xuất tài khoản có phân trang.
     * @param p Kiểu Pageable - Bao gồm số trang và kích thước trang cần lấy
     * @return Page<Account> - Tập hợp dữ liệu tài khoản theo phân trang
     */
    public Page<Account> retrieveAllProfiles(Pageable p) {
        return accountRepository.findAll(p);
    }

    /**
     * Lưu thông tin tài khoản (thêm mới hoặc cập nhật).
     * @param acc Đối tượng Account chứa các thông tin mới hoặc thông tin cập nhật
     * @return void - Không trả về giá trị
     */
    public void commitAccountChange(Account acc) {
        accountRepository.save(acc);
    }

    /**
     * Truy xuất tài khoản phân trang.
     * @param p Kiểu Pageable
     * @return Page<Account>
     */
    public Page<Account> fetchPagedData(Pageable p) {
        return accountRepository.findAll(p);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }

    public List<Account> findByState(AccountState state) {
        return accountRepository.findByState(state);
    }

    public boolean existsByUserName(String userName) {
        return accountRepository.existsByUserName(userName);
    }
}
