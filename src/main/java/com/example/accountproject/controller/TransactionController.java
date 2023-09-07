package com.example.accountproject.controller;

import com.example.accountproject.dto.CancelBalance;
import com.example.accountproject.dto.UseBalance;
import com.example.accountproject.exception.AccountException;
import com.example.accountproject.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;

    /**
     * 잔액 사용 컨트롤러
     * @param request
     * @return 계좌번호, 거래결과, 거래아이디, 거래금액, 거래일시
     */
    @PostMapping("/transaction/use")
    public UseBalance.Response useBalance(@RequestBody @Valid UseBalance.Request request) {
        try {
            return UseBalance.Response.from(transactionService.useBalance(request.getUserId(), request.getAccountNumber(), request.getAmount()));
        } catch (AccountException e) {
            log.error("Failed to use balance.");
            transactionService.saveFailedUseBalance(request.getAccountNumber(), request.getAmount());

            throw e;
        }
    }

    @PostMapping("/transaction/cancel")
    public CancelBalance.Response cancelBalance(@RequestBody @Valid CancelBalance.Request request) {
        try {
            return CancelBalance.Response.from(transactionService.cancelBalance(request.getTransactionId(), request.getAccountNumber(), request.getAmount()));
        } catch (AccountException e) {
            log.error("Failed to cancel balance.");
            transactionService.saveFailedCancelBalance(request.getAccountNumber(), request.getAmount());

            throw e;
        }
    }
}
