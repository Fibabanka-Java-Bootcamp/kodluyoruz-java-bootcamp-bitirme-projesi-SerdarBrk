package org.kodluyoruz.mybank.transaction;

import lombok.*;
import org.kodluyoruz.mybank.account.Account;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    @JoinColumn(name = "account_id",referencedColumnName = "accountId")
    private Account account;
    @Enumerated(value = EnumType.STRING)
    private TransactionType transactionType;
    private String explanation;
    private LocalDate transactionDate;

    public TransactionDto toTransactionDto(){
        return TransactionDto.builder()
                .id(this.id)
                .account(this.account.toAccountDto())
                .transactionType(this.transactionType)
                .explanation(this.explanation)
                .transactionDate(this.transactionDate)
                .build();
    }
}
