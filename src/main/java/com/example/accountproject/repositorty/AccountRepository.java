package com.example.accountproject.repositorty;

import com.example.accountproject.domain.Account;
import com.example.accountproject.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Integer countByAccountUser(AccountUser accountUser);

    Optional<Account> findFirstByOrderByIdDesc();

    Optional<Account> findFirstByOrderByIdAsc();

    Optional<Account> findByAccountNumber(String accountNumber);
}
