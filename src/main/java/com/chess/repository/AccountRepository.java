package com.chess.repository;

import com.chess.model.Account;
import com.chess.model.enums.AccountState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserName(String userName);
    List<Account> findByState(AccountState state);
    List<Account> findByStateNot(AccountState state);
    Page<Account> findAll(Pageable pageable);
    boolean existsByUserName(String userName);
}
