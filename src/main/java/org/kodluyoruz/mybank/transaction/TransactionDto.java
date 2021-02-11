package org.kodluyoruz.mybank.transaction;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.account.AccountDto;

import java.time.LocalDate;


@Builder
@Data
public class TransactionDto {
    private int id;
    private AccountDto account;
    private TransactionType transactionType;
    private String explanation;
    private LocalDate transactionDate;

    public Transaction toTransaction(){
        return Transaction.builder()
                .id(this.id)
                .account(this.account.toAccount())
                .transactionType(this.transactionType)
                .explanation(this.explanation)
                .transactionDate(this.transactionDate)
                .build();
    }
}
