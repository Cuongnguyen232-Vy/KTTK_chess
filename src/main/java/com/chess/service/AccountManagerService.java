package com.chess.service;

import com.chess.model.Account;
import com.chess.persistence.AccountPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AccountManagerService - Xử lý các logic nghiệp vụ quản lý tài khoản.
 * Dùng cho Module III: Quản lý người chơi.
 */
@Service
public class AccountManagerService {

    @Autowired
    private AccountPersistence accountPersistence;

    /**
     * Truy xuất tài khoản có phân trang.
     * @param p Kiểu Pageable - Bao gồm số trang và kích thước trang cần lấy
     * @return Page<Account> - Tập hợp dữ liệu tài khoản theo phân trang
     */
    public Page<Account> retrieveAllProfiles(Pageable p) {
        return accountPersistence.retrieveAllProfiles(p);
    }

    /**
     * Lấy dữ liệu phân trang.
     * @param p Kiểu Pageable
     * @return Page<Account>
     */
    public Page<Account> fetchPagedData(Pageable p) {
        return accountPersistence.fetchPagedData(p);
    }

    /**
     * Lưu thông tin tài khoản.
     * @param acc Đối tượng Account
     */
    public void commitAccountChange(Account acc) {
        accountPersistence.commitAccountChange(acc);
    }

    public Optional<Account> findById(Long id) {
        return accountPersistence.findById(id);
    }

    public Optional<Account> findByUserName(String userName) {
        return accountPersistence.searchByUserName(userName);
    }

    public void deleteById(Long id) {
        accountPersistence.deleteById(id);
    }

    /**
     * Lấy danh sách tất cả người chơi sắp xếp theo ELO giảm dần (dùng cho BXH).
     */
    public List<Account> getAllSortedByElo() {
        return accountPersistence.findAll().stream()
                .sorted(Comparator.comparingInt(Account::getEloPoint).reversed())
                .collect(Collectors.toList());
    }

    public boolean existsByUserName(String userName) {
        return accountPersistence.existsByUserName(userName);
    }
}
