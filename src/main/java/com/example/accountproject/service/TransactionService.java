package com.example.accountproject.service;

import com.example.accountproject.domain.Account;
import com.example.accountproject.domain.AccountUser;
import com.example.accountproject.domain.Transaction;
import com.example.accountproject.dto.TransactionDto;
import com.example.accountproject.exception.AccountException;
import com.example.accountproject.repositorty.AccountRepository;
import com.example.accountproject.repositorty.AccountUserRepository;
import com.example.accountproject.repositorty.TransactionRepository;
import com.example.accountproject.type.AccountStatus;
import com.example.accountproject.type.TransactionResultType;
import com.example.accountproject.type.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.example.accountproject.type.ErrorCode.*;
import static com.example.accountproject.type.ErrorCode.ACCOUNT_ALREADY_UNREGISTERED;
import static com.example.accountproject.type.TransactionResultType.FAIL;
import static com.example.accountproject.type.TransactionResultType.SUCCESS;
import static com.example.accountproject.type.TransactionType.USE;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * 잔액 사용 서비스
     * @param userId
     * @param accountNumber
     * @param amount
     * @return
     * 사용자가 없는 경우, 사용자 아이디와 계좌 소유주가 다른 경우,
     * 계좌가 이미 해지 상태인 경우, 거래금액이 잔액보다 큰 경우,
     * 거래금액이 너무 작거나 큰 경우 실패 응답
     * 거래 정보를 저장
     */
    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber, Long amount) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateUseBalance(accountUser, account, amount);

        account.useBalance(amount);
        return TransactionDto.fromEntity(transactionRepository.save(Transaction.builder()
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactionResultType(SUCCESS)
                        .transactionType(USE)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionAt(LocalDateTime.now())
                .build())
        );
    }

    private void validateUseBalance(AccountUser accountUser, Account account, Long amount) {
        if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UN_MATCH);
        }

        if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }

        if (account.getBalance() < amount) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
    }
}