package org.kodluyoruz.mybank.transaction;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class TransactionDto {
    private int id;
    @NotBlank(message = "Performedid for the transaction is mandatory")
    private UUID performedId;
    private TransactionType transactionType;
    @NotBlank(message = "Performedid for the transaction is mandatory")
    private String explanation;
    @NotBlank(message = "Performedid for the transaction is mandatory")
    private LocalDate transactionDate;

    public Transaction toTransaction(){
        return Transaction.builder()
                .id(this.id)
                .performedId(this.performedId)
                .transactionType(this.transactionType)
                .explanation(this.explanation)
                .transactionDate(this.transactionDate)
                .build();
    }
}
