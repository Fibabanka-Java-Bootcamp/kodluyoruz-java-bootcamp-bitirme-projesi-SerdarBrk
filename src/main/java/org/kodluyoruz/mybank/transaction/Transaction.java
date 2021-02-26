package org.kodluyoruz.mybank.transaction;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

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
    @NotNull(message = "PerformedId for the transaction is mandatory")
    private UUID performedId;
    @Enumerated(value = EnumType.STRING)
    private TransactionType transactionType;
    @NotNull(message = "Explanation for the transaction is mandatory")
    private String explanation;
    @NotNull(message = "PerformedId for the transaction is mandatory")
    private LocalDate transactionDate;

    public TransactionDto toTransactionDto(){
        return TransactionDto.builder()
                .id(this.id)
                .performedId(this.performedId)
                .transactionType(this.transactionType)
                .explanation(this.explanation)
                .transactionDate(this.transactionDate)
                .build();
    }
}
