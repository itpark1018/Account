package com.example.accountproject.controller;

import com.example.accountproject.dto.AccountInfo;
import com.example.accountproject.dto.CreateAccount;
import com.example.accountproject.dto.DeleteAccount;
import com.example.accountproject.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    /**
     * 계좌 생성 컨트롤러
     * @param request
     * @return 사용자 아이디, 계좌 번호, 계좌 등록일시
     */
    @PostMapping("/account")
    public CreateAccount.Response createAccount(@RequestBody @Valid CreateAccount.Request request) {
        return CreateAccount.Response.from(
                accountService.createAccount(request.getUserId(), request.getInitialBalance())
        );
    }

    /**
     * 계좌 해지 컨트롤러
     * @param request
     * @return 사용자 아이디, 계좌 번호, 계좌 해지일시
     */
    @DeleteMapping("/account")
    public DeleteAccount.Response deleteAccount(@RequestBody @Valid DeleteAccount.Request request) {
        return DeleteAccount.Response.from(
                accountService.deleteAccount(request.getUserId(), request.getAccountNumber())
        );
    }

    /**
     * 계좌 확인 컨트롤러
     * @param userId
     * @return 정보(계좌번호, 잔액)를 Json List 형식으로 응답
     */
    @GetMapping("/account")
    public List<AccountInfo> getAccountsInfo(@RequestParam("user_id") Long userId) {
        return accountService.getAccountsInfo(userId).stream()
                .map(accountDto -> AccountInfo.builder()
                        .accountNumber(accountDto.getAccountNumber())
                        .balance(accountDto.getBalance())
                        .build())
                .collect(Collectors.toList());
    }

}