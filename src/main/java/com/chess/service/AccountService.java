package com.chess.service;

import com.chess.model.Account;
import com.chess.model.enums.AccountState;
import com.chess.persistence.AccountPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AccountService - Dịch vụ quản lý trạng thái người chơi.
 * Dùng cho Module IV: Thách đấu trực tiếp.
 */
@Service
public class AccountService {

    @Autowired
    private AccountPersistence accountPersistence;

    /**
     * Lấy danh sách người chơi đang ONLINE.
     * @return List<Account>
     */
    public List<Account> getOnlineAccounts() {
        return accountPersistence.findByState(AccountState.ONLINE);
    }

    /**
     * Cập nhật trạng thái người chơi (ONLINE / OFFLINE / IN_GAME).
     * @param id Long - ID tài khoản
     * @param state AccountState - Trạng thái mới
     */
    public void updateAccountState(Long id, AccountState state) {
        accountPersistence.findById(id).ifPresent(acc -> {
            acc.setState(state);
            accountPersistence.commitAccountChange(acc);
        });
    }

    public void updateAccountStateByUsername(String username, AccountState state) {
        accountPersistence.searchByUserName(username).ifPresent(acc -> {
            acc.setState(state);
            accountPersistence.commitAccountChange(acc);
        });
    }
}
