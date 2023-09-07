package com.example.accountproject.domain;

import com.example.accountproject.exception.AccountException;
import com.example.accountproject.type.AccountStatus;
import com.example.accountproject.type.ErrorCode;
import com.example.accountproject.type.TransactionResultType;
import com.example.accountproject.type.TransactionType;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Transaction extends BaseEntity {
    private String transactionId;

    @ManyToOne
    private Account account;
    private Long amount;
    private Long balanceSnapshot;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @Enumerated(EnumType.STRING)
    private TransactionResultType transactionResultType;

    private LocalDateTime transactionAt;

}
