package com.example.accountproject.dto;

import com.example.accountproject.domain.Transaction;
import com.example.accountproject.type.TransactionResultType;
import com.example.accountproject.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private String transactionId;
    private String accountNumber;
    private Long amount;
    private Long balanceSnapshot;
    private TransactionResultType transactionResultType;
    private TransactionType transactionType;
    private LocalDateTime transactionAt;

    public static TransactionDto fromEntity(Transaction transaction) {
        return TransactionDto.builder()
                .transactionId(transaction.getTransactionId())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .amount(transaction.getAmount())
                .balanceSnapshot(transaction.getBalanceSnapshot())
                .transactionResultType(transaction.getTransactionResultType())
                .transactionType(transaction.getTransactionType())
                .transactionAt(transaction.getTransactionAt())
                .build();
    }
}
