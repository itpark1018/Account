package com.example.accountproject.service;

import com.example.accountproject.domain.Account;
import com.example.accountproject.domain.AccountUser;
import com.example.accountproject.dto.AccountDto;
import com.example.accountproject.exception.AccountException;
import com.example.accountproject.repositorty.AccountRepository;
import com.example.accountproject.repositorty.AccountUserRepository;
import com.example.accountproject.type.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

import static com.example.accountproject.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    /**
     * 계좌 생성 서비스
     * @param userId
     * @param initialBalance
     * @return
     * 사용자가 있는지 조회하고 사용자가 없는 경우 실패 응답
     * 사용자의 계좌가 10개 이상일 때 실패 응답
     * 실패 조건이 없을 경우 계좌 번호를 10자리 랜덤한 정수로 만들고 기본적으로 계좌 번호는 순차 증가 방식으로 생성 됨
     * 단 만들어진 계좌 번호가 10자리가 넘어가는 경우
     * 다시 랜덤한 10자리의 정수로 이루어진 계좌를 만들고 그 계좌부터 다시 번호가 순차 증가 방식으로 생성 됨
     * 만들어진 계좌 정보를 저장
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        validateCreateAccount(accountUser);

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Long.parseLong(account.getAccountNumber())) + 1 + "")
                .orElse(createRandomAccount());

        if (newAccountNumber.length() > 10) {
            newAccountNumber = createRandomAccount();
        }

        return AccountDto.fromEntity(accountRepository.save(Account.builder()
                .accountUser(accountUser)
                .accountNumber(newAccountNumber)
                .accountStatus(AccountStatus.IN_USE)
                .balance(initialBalance)
                .registeredAt(LocalDateTime.now())
                .build())
        );
    }

    private String createRandomAccount() {
        String numericCharacters = "0123456789";

        Random random = new Random();
        StringBuffer accountNumber = new StringBuffer(10);

        for (int i  = 0; i < 10; i++) {
            int index = random.nextInt(numericCharacters.length());
            char randomChar = numericCharacters.charAt(index);
            accountNumber.append(randomChar);
        }

        return accountNumber.toString();
    }

    private void validateCreateAccount(AccountUser accountUser) {
        if (accountRepository.countByAccountUser(accountUser) >= 10) {
            throw new AccountException(MAX_ACCOUNT_PER_USER_10);
        }
    }

    /**
     * 계좌 해지 서비스
     * @param userId
     * @param accountNumber
     * @return
     * 사용자가 없는 경우, 사용자 아이디와 계좌 소유주가 다른 경우,
     * 계좌가 이미 해지 상태인 경우, 잔액이 있는 경우 실패 응답
     * 해지한 계좌 정보를 저장
     */
    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateDeleteAccount(accountUser, account);

        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnRegisteredAt(LocalDateTime.now());

        return AccountDto.fromEntity(account);
    }

    private void validateDeleteAccount(AccountUser accountUser, Account account) {
        if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UN_MATCH);
        }

        if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }

        if (account.getBalance() > 0) {
            throw new AccountException(BALANCE_NOT_EMPTY);
        }
    }
}
